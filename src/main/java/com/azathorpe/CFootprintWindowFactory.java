package com.azathorpe;

import com.azathorpe.utils.fileSystem.FileUtils;
import com.azathorpe.utils.dbUtils.ProblemStore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class CFootprintWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //初始化时 把文件夹都创好
        //create folder when init
        FileUtils.chkDir();
        ProblemStore.updateDatabase();
        ProblemStore.printAllData();
    }
}
