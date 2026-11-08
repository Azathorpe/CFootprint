package com.azathorpe.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.components.labels.LinkLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProblemCell extends JPanel {

    public ProblemCell(List<ProblemDto> problems, Font font) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        setOpaque(true);
        setBackground(UIManager.getColor("Table.background"));

        for (int i = 0; i < problems.size(); i++) {
            ProblemDto p = problems.get(i);
            String url = "https://codeforces.com/contest/" + p.contestId() + "/problem/" + p.index();

            JLabel label = new JLabel(p.index() + ". " + shorten(p.name()));
            label.setToolTipText(p.index() + ". " + p.name()
                    + "  (rating=" + p.rating() + ")\n点击打开: " + url);
            label.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            label.setFont(font);
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        BrowserUtil.browse(url);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
//                    label.setForeground(LinkLabel.);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    label.setForeground(UIManager.getColor("Label.foreground"));
                }
            });
            add(label);
            if (i < problems.size() - 1) {
                add(new JSeparator(JSeparator.HORIZONTAL));
            }
        }
    }

    private static String shorten(String name) {
        if (name == null) return "";
        return name.length() > 24 ? name.substring(0, 24) + "…" : name;
    }
}