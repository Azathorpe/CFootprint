package com.azathorpe.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProblemCell extends JPanel {

    public static final String RATING_COLOR_KEY = "com.azathorpe.plugin.ratingColorEnabled";

    private final List<ProblemDto> problems;
    private final Font font;

    public ProblemCell(List<ProblemDto> problems, Font font) {
        this.problems = problems;
        this.font = font;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false); // 不依赖 Swing 背景，改为 paintComponent 自绘
        setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));

        for (int i = 0; i < problems.size(); i++) {
            ProblemDto p = problems.get(i);
            String url = "https://codeforces.com/contest/" + p.contestId()
                    + "/problem/" + p.index();

            JLabel label = new JLabel(p.index() + ". " + shorten(p.name()));
            label.setToolTipText(p.index() + ". " + p.name()
                    + "  (rating=" + p.rating() + ", " + p.status() + ")\n点击打开: " + url);
            label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            label.setFont(font);
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            label.setForeground(foregroundColor(p));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        BrowserUtil.browse(url);
                    }
                }
            });
            add(label);
            if (i < problems.size() - 1) {
                add(new JSeparator(JSeparator.HORIZONTAL));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 背景色：根据每道题的状态分段绘制（同一格子内可能有多道题）
        int y = 0;
        for (ProblemDto p : problems) {
            int h = getHeight() / problems.size();
            g2.setColor(backgroundColor(p.status()));
            g2.fillRect(0, y, getWidth(), (y + h >= getHeight()) ? getHeight() - y : h);
            y += h;
        }
        g2.dispose();
    }

    /** 背景色由 AC 状态决定 */
    private static Color backgroundColor(ProblemStatus status) {
        return switch (status) {
            case AC -> new JBColor(new Color(0x14532D), new Color(0x14532D));          // 绿色
            case ATTEMPTED -> new JBColor(new Color(0xEAB308), new Color(0xEAB308));   // 黄色
            case UNSOLVED -> UIManager.getColor("Table.background");
        };
    }

    /** 字体色由开关决定：关闭时默认，开启时按 rating 映射 */
    private static Color foregroundColor(ProblemDto p) {
        boolean ratingColorEnabled = PropertiesComponent.getInstance()
                .isTrueValue(RATING_COLOR_KEY);
        if (!ratingColorEnabled) {
            return UIManager.getColor("Label.foreground");
        }
        return ratingToColor(p.rating());
    }

    private static Color ratingToColor(int rating) {
        if (rating < 1200) return new Color(0x808080);       // 灰
        if (rating < 1400) return new Color(0x22C55E);       // 绿
        if (rating < 1600) return new Color(0x06B6D4);       // 青
        if (rating < 1900) return new Color(0x3B82F6);       // 蓝
        if (rating < 2100) return new Color(0xA855F7);       // 紫
        if (rating < 2400) return new Color(0xF97316);       // 橙
        if (rating < 2600) return new Color(0xEF4444);       // 红
        return new Color(0x7F1D1D);                           // 深红/黑红
    }

    private static String shorten(String name) {
        if (name == null) return "";
        return name.length() > 24 ? name.substring(0, 24) + "…" : name;
    }
}