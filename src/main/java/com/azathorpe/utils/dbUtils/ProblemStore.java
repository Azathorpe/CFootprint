package com.azathorpe.utils.dbUtils;

import com.alibaba.fastjson2.JSON;
import com.azathorpe.entities.Contest;
import com.azathorpe.entities.Problem;
import com.azathorpe.entities.ProblemStatistics;
import com.azathorpe.entities.Problems;
import com.azathorpe.utils.cfetcher.CFFetcher;
import com.azathorpe.utils.fileSystem.FileUtils;
import jetbrains.exodus.entitystore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class ProblemStore {
    private static final Logger log = LoggerFactory.getLogger(ProblemStore.class);
    private static long LAST_FETCH_TIMESTAMP = 0;
    private static final long FETCH_INTERVAL_MS = TimeUnit.HOURS.toMillis(6);

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
        try {
            // 因为后面需要用到这俩，所以必须保持同步，不能异步执行
            CFFetcher.fetchProblems().get();
            CFFetcher.fetchContests().get();
        } catch (InterruptedException e) {
            log.info("Interrupted Exception: {}", String.valueOf(e));
        } catch (ExecutionException e) {
            log.info("Execution Exception: {}", String.valueOf(e));
        }
        // 解析json
        // 是两个数组"problems"&"problemStatistics"
        Problems problems = JSON.parseObject(FileUtils.readProblemSet(), Problems.class);
        List<Contest> contests = JSON.parseArray(FileUtils.readContest(), Contest.class);

        // 这个是为了获取他们的通过数量，我们单独放到xodus里面
        HashMap<String, Integer> problemStatisticsMap = new HashMap<>();
        for (ProblemStatistics ps : problems.getProblemStatistics())
            problemStatisticsMap.put(ps.getContestId() + "-" + ps.getIndex(), ps.getSolvedCount());
        HashMap<Integer, String> contestIdToNameMap = new HashMap<>();
        for (Contest contest : contests)
            contestIdToNameMap.put(contest.getId(), contest.getName());

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
                String contestName = contestIdToNameMap.get(problemS.getContestId());
                contest.setProperty("name", contestName == null ? "" : contestName);
                contest.setProperty("division", classifyDivision(contestName));

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

    private static String classifyDivision(String name) {
        if (name == null) return "OTHER";
        String n = name.toLowerCase();
        boolean div1 = n.contains("div. 1") || n.contains("div.1") || n.contains("div 1");
        boolean div2 = n.contains("div. 2") || n.contains("div.2") || n.contains("div 2");
        if (div1 && div2) return "DIV1_2";
        if (div1) return "DIV1";
        if (div2) return "DIV2";
        if (n.contains("div. 3") || n.contains("div.3") || n.contains("div 3")) return "DIV3";
        if (n.contains("div. 4") || n.contains("div.4") || n.contains("div 4")) return "DIV4";
        if (n.contains("educational")) return "EDU";
        if (n.contains("global round")) return "GLOBAL";
        return "OTHER";
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
