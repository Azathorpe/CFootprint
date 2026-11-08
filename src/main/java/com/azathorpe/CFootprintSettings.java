package com.azathorpe;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@State(name = "CFootprintSettings", storages = @Storage("cfootprint.xml"))
public class CFootprintSettings implements PersistentStateComponent<CFootprintSettings.State> {

    public static class State {
        public String fontFamily = "";   // 空表示跟随 IDE 默认
        public int fontSizeOffset = 0;   // 相对默认字号偏移，官方推荐做法
    }

    private State state = new State();

    public static CFootprintSettings getInstance() {
        return ApplicationManager.getApplication().getService(CFootprintSettings.class);
    }

    @Override public State getState() { return state; }
    @Override public void loadState(@NotNull State state) { this.state = state; }

    /** 统一获取字体：基于 IDE 当前 UI 字体 + 用户偏移 */
    public Font getMatrixFont() {
        Font base = UIManager.getFont("Label.font"); // 跟随 IDE 主题
        int size = base.getSize() + state.fontSizeOffset;
        if (state.fontFamily != null && !state.fontFamily.isEmpty()) {
            return new Font(state.fontFamily, Font.PLAIN, size);
        }
        return base.deriveFont((float) size);
    }
}