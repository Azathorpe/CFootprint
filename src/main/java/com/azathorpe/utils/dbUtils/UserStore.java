package com.azathorpe.utils.dbUtils;

import com.alibaba.fastjson2.JSON;
import com.azathorpe.entities.Submission;
import com.azathorpe.utils.dbUtils.ProblemStore;
import com.azathorpe.utils.cfetcher.CFFetcher;
import com.azathorpe.utils.fileSystem.FileUtils;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStore {

    /**
     * 拉取指定 handle 的提交记录，聚合出每道题的最佳状态，写入 Problem 实体的 verdict 属性。
     * 不创建任何新的 Problem / Contest 实体——题库由 ProblemStore.updateDatabase 负责。
     */
    public static void updateDatabase(String handle) {
        // 1. 拉取并解析
        CFFetcher.fetchUserStatus(handle);
        List<Submission> submissions = JSON.parseArray(
                FileUtils.readUserStatus(handle), Submission.class);
        if (submissions == null || submissions.isEmpty()) {
            return;
        }

        // 2. 聚合：每道题只保留一个“最佳”提交
        //    规则：OK 永远优先；已有 OK 时不再被覆盖；否则保留先遇到的（列表通常最新在前）
        Map<String, Submission> best = new HashMap<>();
        for (Submission s : submissions) {
            if (s.getProblem() == null || s.getProblem().getIndex() == null) {
                continue;
            }
            String key = s.getContestId() + "-" + s.getProblem().getIndex();
            Submission existing = best.get(key);
            if (existing == null
                    || (existing.getVerdict() != Submission.Verdict.OK
                    && s.getVerdict() == Submission.Verdict.OK)) {
                best.put(key, s);
            }
        }

        // 3. 写入 Problem 实体（使用 ProblemStore 的数据库）
        ProblemStore.get().executeInTransaction(txn -> {
            for (Submission s : best.values()) {
                Entity problem = findProblem(txn,
                        s.getProblem().getContestId(), s.getProblem().getIndex());
                if (problem == null) {
                    // 题库里没有这道题，跳过，不创建残缺实体
                    continue;
                }
                problem.setProperty("verdict",
                        s.getVerdict() == null ? "UNKNOWN" : s.getVerdict().name());
            }
        });
    }

    /** 按 (contestId, index) 查找已有 Problem 实体；找不到返回 null */
    private static Entity findProblem(StoreTransaction txn, int contestId, String index) {
        for (Entity e : txn.find("Problem", "contestId", contestId)) {
            if (index.equals(e.getProperty("index"))) {
                return e;
            }
        }
        return null;
    }
}