package com.azathorpe.utils.dbUtils;

import com.azathorpe.utils.cfetcher.CFFetcher;
import com.azathorpe.utils.fileSystem.FileUtils;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;

public class ContestStore {
    private static PersistentEntityStore instance;

    public static synchronized PersistentEntityStore get() {
        if (instance == null) {
            instance = PersistentEntityStores.newInstance(FileUtils.ProblemStoragePath.resolve("contestStore").toFile());
        }
        return instance;
    }

    public static synchronized void close() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public static void updateDatabase() {
        // 拉取题目文件
        CFFetcher.fetchContests();
    }
}
