package com.azathorpe.services;

import com.azathorpe.utils.fileSystem.FileUtils;
import com.intellij.openapi.Disposable;
import com.jetbrains.Service;

@Service
public class ShutdownService implements Disposable {
    @Override
    public void dispose() {
        FileUtils.saveCommonConfig();
    }
}
