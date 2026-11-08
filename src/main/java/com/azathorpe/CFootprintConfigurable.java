package com.azathorpe;

import com.azathorpe.settings.FontChangedNotifier;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CFootprintConfigurable implements Configurable {

    private JComboBox<String> fontFamilyBox;
    private JSpinner fontSizeSpinner;

    @Override
    public @NotNull String getDisplayName() { return "CFootprint"; }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("字体:"));
        fontFamilyBox = new ComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
        panel.add(fontFamilyBox);
        panel.add(new JLabel("字号偏移:"));
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(0, -3, 10, 1));
        panel.add(fontSizeSpinner);
        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        CFootprintSettings.State s = CFootprintSettings.getInstance().getState();
        return !fontFamilyBox.getSelectedItem().equals(s.fontFamily)
                || (int) fontSizeSpinner.getValue() != s.fontSizeOffset;
    }

    @Override
    public void apply() {
        CFootprintSettings.State s = CFootprintSettings.getInstance().getState();
        s.fontFamily = (String) fontFamilyBox.getSelectedItem();
        s.fontSizeOffset = (int) fontSizeSpinner.getValue();

        // 通知所有订阅者
        ApplicationManager.getApplication().getMessageBus()
                .syncPublisher(FontChangedNotifier.TOPIC)
                .fontChanged();
    }

    @Override
    public void reset() {
        CFootprintSettings.State s = CFootprintSettings.getInstance().getState();
        fontFamilyBox.setSelectedItem(s.fontFamily);
        fontSizeSpinner.setValue(s.fontSizeOffset);
    }
}