package com.azathorpe.ui;

import com.azathorpe.CFootprintSettings;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ProblemMatrixPanel extends JPanel {

    public ProblemMatrixPanel(ProblemMatrixBuilder.MatrixData data) {
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(1, 1, 1, 1);

        // 表头
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0;
        JLabel corner = headerLabel("Contest");
        corner.setPreferredSize(new Dimension(70, 28));
        add(corner, c);

        List<String> baseIndexes = data.baseIndexes();
        for (int i = 0; i < baseIndexes.size(); i++) {
            c.gridx = i + 1;
            c.weightx = 1;
            add(headerLabel(baseIndexes.get(i)), c);
        }

        // 数据行
        int rowIdx = 1;
        for (Map.Entry<Integer, Map<String, List<ProblemDto>>> row : data.rows().entrySet()) {
            c.gridy = rowIdx++;
            c.gridx = 0;
            c.weightx = 0;
            JLabel contestLabel = new JLabel(String.valueOf(row.getKey()));
            contestLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            contestLabel.setFont(contestLabel.getFont().deriveFont(Font.BOLD));
            add(contestLabel, c);

            for (int i = 0; i < baseIndexes.size(); i++) {
                c.gridx = i + 1;
                c.weightx = 1;
                List<ProblemDto> cell = row.getValue().get(baseIndexes.get(i));
                if (cell == null || cell.isEmpty()) {
                    JPanel empty = new JPanel();
                    empty.setBackground(UIManager.getColor("Table.background"));
                    empty.setBorder(BorderFactory.createLineBorder(new Color(0xEEEEEE)));
                    add(empty, c);
                } else {
                    add(new ProblemCell(cell, CFootprintSettings.getInstance().getMatrixFont()), c);
                }
            }
        }

        // 底部弹簧，防止行被纵向拉伸
        c.gridy = rowIdx;
        c.gridx = 0;
        c.gridwidth = baseIndexes.size() + 1;
        c.weighty = 1;
        add(new JPanel(), c);
    }

    private static JLabel headerLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(CFootprintSettings.getInstance().getMatrixFont().deriveFont(Font.BOLD));
        l.setOpaque(true);
        l.setBackground(UIManager.getColor("TableHeader.background"));
        l.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return l;
    }
}