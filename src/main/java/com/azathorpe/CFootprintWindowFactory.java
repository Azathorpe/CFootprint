package com.azathorpe;

import com.azathorpe.settings.FontChangedNotifier;
import com.azathorpe.store.ProblemQueries;
import com.azathorpe.ui.*;
import com.azathorpe.utils.dbUtils.ContestStore;
import com.azathorpe.utils.dbUtils.ProblemStore;
import com.azathorpe.utils.dbUtils.UserStore;
import com.azathorpe.utils.fileSystem.FileUtils;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.WrapLayout;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.azathorpe.ui.ProblemCell.RATING_COLOR_KEY;

public class CFootprintWindowFactory implements ToolWindowFactory {

    private static final String HANDLE_KEY = "com.azathorpe.plugin.handle";
    private static final String LAST_FETCH_PREFIX = "com.azathorpe.plugin.lastFetch.";
    private static final long FETCH_INTERVAL_MS = 6 * 60 * 60 * 1000L; // 6 小时

    private JBTextField handleField;
    private JButton loadBtn;

    private static final Logger log = LoggerFactory.getLogger(CFootprintWindowFactory.class);
    private int CONTESTS_PER_PAGE = 10;

    // 筛选控件
    private JBTextField nameField;
    private JBTextField ratingMinField;
    private JBTextField ratingMaxField;
    private JComboBox<String> typeBox;
    private JBTextField tagsField;
    private JComboBox<String> divBox;

    // 分页控件
    private JLabel pageLabel;
    private JButton prevBtn;
    private JButton nextBtn;
    private JBScrollPane matrixScroll;

    // 状态
    private Paginator<Integer> contestPaginator;
    private List<ProblemDto> currentFilteredProblems = List.of();
    private Map<String, String> currentVerdicts = Map.of();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        FileUtils.chkDir();
        FileUtils.parseCommonConfig();

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (ProblemStore.autoUpdate()) {
                log.info("Problem Updated...");
                System.out.println("Problem Updated...");
            }

            if (ContestStore.autoUpdate()) {
                log.info("Contest Updated...");
                System.out.println("Contest Updated...");
            }
        });


        JPanel root = new JPanel(new BorderLayout());
        root.add(createFilterBar(project), BorderLayout.NORTH);
        root.add(createMatrixArea(), BorderLayout.CENTER);
        root.add(createPaginationBar(), BorderLayout.SOUTH);

        var content = toolWindow.getContentManager().getFactory()
                .createContent(root, "Codeforces", false);
        toolWindow.getContentManager().addContent(content);

        // 首次加载
        refreshAll(null);

        ApplicationManager.getApplication().getMessageBus().connect(content)
                .subscribe(FontChangedNotifier.TOPIC, (FontChangedNotifier) () -> ApplicationManager.getApplication().invokeLater(
                        CFootprintWindowFactory.this::renderCurrentPage));
    }

    /**
     * 顶部筛选条
     */
    private JPanel createFilterBar(Project project) {
        JPanel bar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));

        // ===== Handle 区域 =====
        bar.add(new JLabel("Handle:"));
        handleField = new JBTextField(14);
        handleField.getEmptyText().setText("Codeforces handle");
        handleField.setText(PropertiesComponent.getInstance().getValue(HANDLE_KEY, ""));
        bar.add(handleField);

        loadBtn = new JButton("加载");
        loadBtn.addActionListener(e -> loadHandle(project));
        bar.add(loadBtn);

        bar.add(new JSeparator(JSeparator.VERTICAL));

        bar.add(new JLabel("Name:"));
        nameField = new JBTextField(12);
        nameField.getEmptyText().setText("题目名关键词");
        bar.add(nameField);

        bar.add(new JLabel("Rating:"));
        ratingMinField = new JBTextField(4);
        ratingMinField.getEmptyText().setText("min");
        ratingMaxField = new JBTextField(4);
        ratingMaxField.getEmptyText().setText("max");
        bar.add(ratingMinField);
        bar.add(new JLabel("~"));
        bar.add(ratingMaxField);

        bar.add(new JLabel("Type:"));
        typeBox = new ComboBox<>(new String[]{"全部", "PROGRAMMING", "QUESTION"});
        bar.add(typeBox);

        bar.add(new JLabel("Tags:"));
        tagsField = new JBTextField(10);
        tagsField.getEmptyText().setText("dp,greedy");
        bar.add(tagsField);

        bar.add(new JLabel("Div:"));
        divBox = new ComboBox<>(new String[]{
                "全部", "DIV1", "DIV2", "DIV3", "DIV4", "DIV1_2", "EDU", "GLOBAL", "OTHER"
        });
        bar.add(divBox);

        JCheckBox ratingColorBox = new JCheckBox("按难度着色",
                PropertiesComponent.getInstance().isTrueValue(RATING_COLOR_KEY));
        ratingColorBox.addActionListener(e -> {
            PropertiesComponent.getInstance().setValue(RATING_COLOR_KEY, ratingColorBox.isSelected());
            // 触发矩阵重建，让新着色生效
            ApplicationManager.getApplication().getMessageBus()
                    .syncPublisher(FontChangedNotifier.TOPIC).fontChanged();
        });
        bar.add(ratingColorBox);

        JButton applyBtn = new JButton("筛选");
        applyBtn.addActionListener(e -> refreshAll(buildFilter()));
        bar.add(applyBtn);

        JButton resetBtn = new JButton("重置");
        resetBtn.addActionListener(e -> {
            nameField.setText("");
            ratingMinField.setText("");
            ratingMaxField.setText("");
            typeBox.setSelectedIndex(0);
            tagsField.setText("");
            divBox.setSelectedIndex(0);
            refreshAll(null);
        });
        bar.add(resetBtn);

        return bar;
    }

    private void loadHandle(Project project) {
        String handle = handleField.getText().trim();
        if (handle.isEmpty()) return;

        PropertiesComponent props = PropertiesComponent.getInstance();
        props.setValue(HANDLE_KEY, handle);

        String fetchKey = LAST_FETCH_PREFIX + handle;
        long lastFetch = props.getLong(fetchKey, 0);
        boolean needFetch = System.currentTimeMillis() - lastFetch >= FETCH_INTERVAL_MS;

        loadBtn.setEnabled(false);
        new Task.Backgroundable(project, "加载 " + handle + " 的提交记录", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                if (needFetch) {
                    indicator.setText("正在从 Codeforces 拉取数据…");
                    // UserStore.updateDatabase 内部会 fetch，这里不重复调
                } else {
                    indicator.setText("使用本地缓存…");
                }
                UserStore.updateDatabase(handle, needFetch);
                if (needFetch) {
                    props.setValue(fetchKey, String.valueOf(System.currentTimeMillis()));
                }
            }

            @Override
            public void onSuccess() {
                renderCurrentPage();
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                NotificationGroupManager.getInstance()
                        .getNotificationGroup("CFootprint")
                        .createNotification("加载失败: " + error.getMessage(),
                                NotificationType.ERROR)
                        .notify(project);
            }

            @Override
            public void onFinished() {
                loadBtn.setEnabled(true);
            }
        }.queue();
    }

    /**
     * 矩阵区域（滚动）
     */
    private JComponent createMatrixArea() {
        matrixScroll = new JBScrollPane();
        matrixScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return matrixScroll;
    }

    /**
     * 底部分页条
     */
    private JPanel createPaginationBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));

        // 每页数量
        bar.add(new JLabel("每页:"));
        JComboBox<Integer> pageSizeBox = new JComboBox<>(new Integer[]{5, 10, 20, 50});
        pageSizeBox.setSelectedItem(CONTESTS_PER_PAGE);
        pageSizeBox.addActionListener(e -> {
            Integer size = (Integer) pageSizeBox.getSelectedItem();
            if (size != null && size != CONTESTS_PER_PAGE) {
                CONTESTS_PER_PAGE = size;
                // 保持当前页尽量不变：先记录当前页起始 contest，再重建分页器
                rebuildPaginatorKeepingPosition();
            }
        });
        bar.add(pageSizeBox);

        // 上一页 / 页码 / 下一页
        prevBtn = new JButton("上一页");
        nextBtn = new JButton("下一页");
        pageLabel = new JLabel();

        prevBtn.addActionListener(e -> {
            if (contestPaginator != null && contestPaginator.hasPrev()) {
                contestPaginator.goTo(contestPaginator.currentPage() - 1);
                renderCurrentPage();
            }
        });
        nextBtn.addActionListener(e -> {
            if (contestPaginator != null && contestPaginator.hasNext()) {
                contestPaginator.goTo(contestPaginator.currentPage() + 1);
                renderCurrentPage();
            }
        });

        bar.add(prevBtn);
        bar.add(pageLabel);
        bar.add(nextBtn);

        // 跳转
        bar.add(new JLabel("跳转:"));
        JBTextField jumpField = new JBTextField(4);
        jumpField.getEmptyText().setText("页码");
        JButton jumpBtn = new JButton("Go");
        jumpBtn.addActionListener(e -> {
            String text = jumpField.getText().trim();
            if (text.isEmpty() || contestPaginator == null) return;
            try {
                int page = Integer.parseInt(text);   // 用户输入从 1 开始
                contestPaginator.goTo(page - 1);
                renderCurrentPage();
                jumpField.setText("");
            } catch (NumberFormatException ignored) {
            }
        });
        jumpField.addActionListener(e -> jumpBtn.doClick()); // 回车即跳转
        bar.add(jumpField);
        bar.add(jumpBtn);

        return bar;
    }

    private void rebuildPaginatorKeepingPosition() {
        if (contestPaginator == null) return;

        // 记录当前页第一个 contest
        List<Integer> currentItems = contestPaginator.currentItems();
        Integer anchorContestId = currentItems.isEmpty() ? null : currentItems.get(0);

        // 从全量筛选结果重新提取 contest 列表
        List<Integer> contestIds = currentFilteredProblems.stream()
                .map(ProblemDto::contestId)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        contestPaginator = new Paginator<>(contestIds, CONTESTS_PER_PAGE);

        // 找到 anchor 所在的新页
        if (anchorContestId != null) {
            int index = contestIds.indexOf(anchorContestId);
            if (index >= 0) {
                contestPaginator.goTo(index / CONTESTS_PER_PAGE);
            }
        }
        renderCurrentPage();
    }

    /**
     * 重新筛选 + 重新分页 + 渲染第一页
     */
    private void refreshAll(ProblemFilter filter) {
        String handle = PropertiesComponent.getInstance().getValue(HANDLE_KEY, "");
        currentVerdicts = handle.isEmpty()
                ? Map.of()
                : UserStore.loadAllVerdicts(handle);

        List<ProblemDto> data = (filter == null || filter.isEmpty())
                ? ProblemQueries.loadAll(currentVerdicts)
                : ProblemQueries.query(filter, currentVerdicts);
        currentFilteredProblems = data;

        // 按 contestId 排序后提取唯一 contest 列表
        List<Integer> contestIds = data.stream()
                .map(ProblemDto::contestId)
                .distinct()
                .sorted(Comparator.reverseOrder())   // 改成降序
                .collect(Collectors.toList());

        contestPaginator = new Paginator<>(contestIds, CONTESTS_PER_PAGE);
        renderCurrentPage();
    }

    /**
     * 渲染当前页
     */
    private void renderCurrentPage() {
        if (contestPaginator == null) return;

        List<Integer> pageContests = contestPaginator.currentItems();
        // 只取当前页 contest 对应的题目
        List<ProblemDto> pageProblems = currentFilteredProblems.stream()
                .filter(p -> pageContests.contains(p.contestId()))
                .collect(Collectors.toList());

        ProblemMatrixBuilder.MatrixData matrixData = ProblemMatrixBuilder.build(pageProblems);
        ProblemMatrixPanel panel = new ProblemMatrixPanel(matrixData);

        SwingUtilities.invokeLater(() -> {
            matrixScroll.setViewportView(panel);
            matrixScroll.revalidate();
            matrixScroll.repaint();
            updatePaginationControls();
        });
    }

    private void updatePaginationControls() {
        int cur = contestPaginator.currentPage() + 1;
        int total = contestPaginator.pageCount();
        pageLabel.setText("第 " + cur + " / " + total + " 页");
        prevBtn.setEnabled(contestPaginator.hasPrev());
        nextBtn.setEnabled(contestPaginator.hasNext());
    }

    private ProblemFilter buildFilter() {
        String name = nameField.getText().trim();
        Integer min = parseIntOrNull(ratingMinField.getText().trim());
        Integer max = parseIntOrNull(ratingMaxField.getText().trim());
        String type = (String) typeBox.getSelectedItem();
        List<String> tags = new ArrayList<>();
        String tagsText = tagsField.getText().trim();
        if (!tagsText.isEmpty()) {
            Arrays.stream(tagsText.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(tags::add);
        }
        String division = (String) divBox.getSelectedItem();
        return new ProblemFilter(
                null,
                name.isEmpty() ? null : name,
                min, max, type, tags,
                division
        );
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}