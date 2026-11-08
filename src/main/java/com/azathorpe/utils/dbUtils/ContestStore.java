package com.azathorpe.utils.dbUtils;

import com.azathorpe.utils.cfetcher.CFFetcher;
import com.azathorpe.utils.fileSystem.FileUtils;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;

import java.util.concurrent.TimeUnit;

public class ContestStore {
    private static long LAST_FETCH_TIMESTAMP = 0;
    private static final long FETCH_INTERVAL_MS = TimeUnit.HOURS.toMillis(6);

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

    /**
     * 自动抓取数据(需要两次需要间隔6个小时)
     * @return 是否抓取数据
     */
    public static boolean autoUpdate(){
        long currentTimeMillis = System.currentTimeMillis();
        long distance = currentTimeMillis - LAST_FETCH_TIMESTAMP;
        if(distance >= FETCH_INTERVAL_MS){
            //间隔在6个小时，可以抓数据
            updateDatabase();
            LAST_FETCH_TIMESTAMP = currentTimeMillis;
            return true;
        }
        return false;
    }
}
