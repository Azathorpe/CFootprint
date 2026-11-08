package com.azathorpe.utils.dbUtils;

import com.alibaba.fastjson2.JSON;
import com.azathorpe.entities.Submission;
import com.azathorpe.utils.cfetcher.CFFetcher;
import com.azathorpe.utils.fileSystem.FileUtils;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

public final class UserStore {

    private static final Logger log = LoggerFactory.getLogger(UserStore.class);

    private static PersistentEntityStore instance;

    public static synchronized PersistentEntityStore get() {
        if (instance == null) {
            instance = PersistentEntityStores.newInstance(
                    FileUtils.UserStoragePath.resolve("userStore").toFile());
        }
        return instance;
    }

    public static synchronized void close() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    /**
     * 拉取指定 handle 的提交记录，聚合出每道题的最佳状态，
     * 写入按 handle 隔离的 UserProblemStatus 实体（不碰全局 Problem）。
     */
    public static void updateDatabase(String handle, boolean needFetch) {
        try {
            if (needFetch)
                CFFetcher.fetchUserStatus(handle).join();
        } catch (CompletionException e) {
            log.error("拉取 {} 的 submission 失败", handle, e);
            return;
        }

        List<Submission> submissions = JSON.parseArray(
                FileUtils.readUserStatus(handle), Submission.class);
        if (submissions == null || submissions.isEmpty()) {
            return;
        }

        // 新数据内部聚合：OK 优先，否则保留先遇到的
        Map<String, Submission> best = new HashMap<>();
        for (Submission s : submissions) {
            if (s.getProblem() == null || s.getProblem().getIndex() == null) continue;
            String key = s.getContestId() + "-" + s.getProblem().getIndex();
            Submission existing = best.get(key);
            if (existing == null
                    || (existing.getVerdict() != Submission.Verdict.OK
                    && s.getVerdict() == Submission.Verdict.OK)) {
                best.put(key, s);
            }
        }

        UserStore.get().executeInTransaction(txn -> {
            for (Submission s : best.values()) {
                int contestId = s.getContestId();
                String index = s.getProblem().getIndex();

                Entity status = findUserProblemStatus(txn, handle, contestId, index);
                if (status == null) {
                    status = txn.newEntity("UserProblemStatus");
                    status.setProperty("handle", handle);
                    status.setProperty("contestId", contestId);
                    status.setProperty("index", index);
                }

                String local = (String) status.getProperty("verdict");
                boolean localAC = "OK".equals(local);
                boolean newAC = s.getVerdict() == Submission.Verdict.OK;

                if (localAC) continue;
                if (newAC || local == null) {
                    status.setProperty("verdict",
                            s.getVerdict() == null ? "UNKNOWN" : s.getVerdict().name());
                }
            }
        });
    }

    /**
     * 查询某个 handle 在某道题上的状态；没有记录返回 null
     */
    public static String getVerdict(String handle, int contestId, String index) {
        String[] result = new String[1];
        UserStore.get().executeInReadonlyTransaction(txn -> {
            Entity status = findUserProblemStatus(txn, handle, contestId, index);
            if (status != null) {
                result[0] = (String) status.getProperty("verdict");
            }
        });
        return result[0];
    }

    /**
     * 查询某个 handle 的全部状态，返回 key = contestId + "-" + index 的映射。
     * 适合一次性加载后在内存里查，避免每道题都开事务。
     */
    public static Map<String, String> loadAllVerdicts(String handle) {
        Map<String, String> map = new HashMap<>();
        UserStore.get().executeInReadonlyTransaction(txn -> {
            for (Entity e : txn.find("UserProblemStatus", "handle", handle)) {
                String key = e.getProperty("contestId") + "-" + e.getProperty("index");
                String verdict = (String) e.getProperty("verdict");
                if (verdict != null) {
                    map.put(key, verdict);
                }
            }
        });
        return map;
    }

    private static Entity findUserProblemStatus(StoreTransaction txn, String handle,
                                                int contestId, String index) {
        for (Entity e : txn.find("UserProblemStatus", "handle", handle)) {
            if (Integer.valueOf(contestId).equals(e.getProperty("contestId"))
                    && index.equals(e.getProperty("index"))) {
                return e;
            }
        }
        return null;
    }
}