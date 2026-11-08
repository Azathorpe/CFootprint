package com.azathorpe.utils.dbUtils;

import com.alibaba.fastjson2.JSON;
import com.azathorpe.entities.Problem;
import com.azathorpe.entities.ProblemStatistics;
import com.azathorpe.entities.Problems;
import com.azathorpe.utils.cfetcher.CFFetcher;
import com.azathorpe.utils.fileSystem.FileUtils;
import jetbrains.exodus.entitystore.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProblemStore {
    private static PersistentEntityStore instance;

    public static synchronized PersistentEntityStore get() {
        if (instance == null) {
            instance = PersistentEntityStores.newInstance(FileUtils.ProblemStoragePath.resolve("problemStore").toFile());
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
     * 更新数据库(通过json文件)
     */
    public static void updateDatabase() {
        // 拉取题目文件
        CFFetcher.fetchProblems();
        // 解析json
        // 是两个数组"problems"&"problemStatistics"
        Problems problems = JSON.parseObject(FileUtils.readProblemSet(), Problems.class);
        // 这个是为了获取他们的通过数量，我们单独放到xodus里面
        HashMap<String, Integer> problemStatisticsMap = new HashMap<>();
        for (ProblemStatistics ps : problems.getProblemStatistics())
            problemStatisticsMap.put(ps.getContestId() + "-" + ps.getIndex(), ps.getSolvedCount());

        ProblemStore.get().executeInTransaction(txn -> {
            for (Problem problemS : problems.getProblems()) {
                Entity contest = null;
                EntityIterable contestIt = txn.find("Contest", "contestId", problemS.getContestId());
                for (Entity e : contestIt) {
                    contest = e;
                    break;
                }
                if (contest == null) {
                    //如果没有的话 那么就创建一个新的实体
                    contest = txn.newEntity("Contest");
                    contest.setProperty("contestId", problemS.getContestId());
                }

                Entity problemDB = findProblem(txn, problemS.getContestId(), problemS.getIndex());
                if (problemDB == null) {
                    problemDB = txn.newEntity("Problem");
                    problemDB.setProperty("contestId", problemS.getContestId());
                    problemDB.setProperty("index", problemS.getIndex());
                    problemDB.setLink("contest", contest);
                }
                // 无条件更新可变属性
                problemDB.setProperty("name", problemS.getName());
                problemDB.setProperty("type", problemS.getType());
                problemDB.setProperty("points", problemS.getPoints());
                problemDB.setProperty("rating", problemS.getRating());

                Entity statDB = findStat(txn, problemS.getContestId(), problemS.getIndex());
                if (statDB == null) {
                    statDB = txn.newEntity("ProblemStatistics");
                    statDB.setProperty("contestId", problemS.getContestId());
                    statDB.setProperty("index", problemS.getIndex());
                    statDB.setLink("problem", problemDB);
                }
                Integer cnt = problemStatisticsMap.get(problemS.getContestId() + "-" + problemS.getIndex());
                statDB.setProperty("solvedCount", cnt == null ? 0 : cnt);  // 新建和更新都走这里
            }
        });
    }

    public static void printAllData() {
        ProblemStore.get().executeInReadonlyTransaction(txn -> {
            System.out.println("========== Contest ==========");
            for (Entity contest : txn.getAll("Contest")) {
                System.out.println("contestId=" + contest.getProperty("contestId"));
            }

            System.out.println("========== Problem ==========");
            int problemCount = 0;
            for (Entity p : txn.getAll("Problem")) {
                problemCount++;
                System.out.println(
                        "contestId=" + p.getProperty("contestId")
                                + ", index=" + p.getProperty("index")
                                + ", name=" + p.getProperty("name")
                                + ", type=" + p.getProperty("type")
                                + ", points=" + p.getProperty("points")
                                + ", rating=" + p.getProperty("rating")
                );
            }
            System.out.println("Problem 总数: " + problemCount);

            System.out.println("========== ProblemStatistics ==========");
            int statCount = 0;
            for (Entity s : txn.getAll("ProblemStatistics")) {
                statCount++;
                System.out.println(
                        "contestId=" + s.getProperty("contestId")
                                + ", index=" + s.getProperty("index")
                                + ", solvedCount=" + s.getProperty("solvedCount")
                );
            }
            System.out.println("ProblemStatistics 总数: " + statCount);
        });
    }

    private static Entity findProblem(StoreTransaction txn, int contestId, String index) {
        for (Entity e : txn.find("Problem", "contestId", contestId)) {
            if (index.equals(e.getProperty("index"))) {
                return e;
            }
        }
        return null;
    }

    private static Entity findStat(StoreTransaction txn, int contestId, String index) {
        for (Entity e : txn.find("ProblemStatistics", "contestId", contestId)) {
            if (index.equals(e.getProperty("index"))) {
                return e;
            }
        }
        return null;
    }
}
