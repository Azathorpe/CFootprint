package com.azathorpe.utils.fileSystem;

import com.alibaba.fastjson2.JSON;
import com.azathorpe.entities.CommonConfig;
import com.azathorpe.utils.dbUtils.ContestStore;
import com.azathorpe.utils.dbUtils.ProblemStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static CommonConfig cc = new CommonConfig();

    public static final String userDir = System.getProperty("user.home");
    public static final Path userPath = Path.of(System.getProperty("user.home"));
    public static final String FileSystemBaseDir = Paths.get(userDir).resolve(".cfp").toString();
    public static final Path FileSystemBasePath = Paths.get(userDir).resolve(".cfp");
    public static final String ProblemStorageDir = Paths.get(FileSystemBaseDir).resolve("problems").toString();
    public static final Path ProblemStoragePath = Paths.get(FileSystemBaseDir).resolve("problems");
    public static final String ContestStorageDir = Paths.get(FileSystemBaseDir).resolve("contests").toString();
    public static final Path ContestStoragePath = Paths.get(FileSystemBaseDir).resolve("contests");
    public static final String UserStorageDir = Paths.get(FileSystemBaseDir).resolve("users").toString();
    public static final Path UserStoragePath = Paths.get(FileSystemBaseDir).resolve("users");
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 确保所有文件目录和文件存在
     * 我们使用的是:
     * FileSystemBaseDir: ${userDir}/.cfp/
     * ProblemStorageDir: ${FileSystemBaseDir}/problems
     * UserStorageDir: ${FileSystemBaseDir}/users
     * UserData: ${UserStorage}/${handle}
     */
    public static void chkDir() {
        try {
            log.info("Folder Checking......");
            Files.createDirectories(userPath);
            Files.createDirectories(FileSystemBasePath);
            Files.createDirectories(ProblemStoragePath);
            Files.createDirectories(ContestStoragePath);
            Files.createDirectories(UserStoragePath);
            log.info("Checking Over......");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCommonConfig() {
        try {
            FileWriter fw = new FileWriter(FileSystemBasePath.resolve("lasttimestamp.json").toFile());
            String content = JSON.toJSONString(cc);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseCommonConfig() {
        //先校验文件是否存在
        if (!Files.exists(FileSystemBasePath.resolve("lasttimestamp.json"))) {
            // 不存在的话，那就call一下save
            saveCommonConfig();
        }

        try {
            // 读取文件
            String content = Files.readString(FileSystemBasePath.resolve("lasttimestamp.json"));
            CommonConfig commonConfig = JSON.parseObject(content, CommonConfig.class);
            ProblemStore.LAST_FETCH_TIMESTAMP = commonConfig.getProblemLastFetchTimeStamp();
            ContestStore.LAST_FETCH_TIMESTAMP = commonConfig.getContestLastFetchTimeStamp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 保存用户的状态
     *
     * @param handle handle
     */
    public static void saveUserStatus(String handle, String content) {
        try {
            FileWriter fw = new FileWriter(UserStoragePath.resolve(handle + ".json").toFile());
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readUserStatus(String handle) {
        try {
            return Files.readString(UserStoragePath.resolve(handle + ".json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存题目集
     *
     * @param content 内容
     */
    public static void saveProblemSet(String content) {
        try {
            FileWriter fw = new FileWriter(ProblemStoragePath.resolve("problemset.json").toFile());
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readProblemSet() {
        try {
            return Files.readString(ProblemStoragePath.resolve("problemset.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveContest(String content) {
        try {
            FileWriter fw = new FileWriter(ContestStoragePath.resolve("contestset.json").toFile());
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readContest() {
        try {
            return Files.readString(ContestStoragePath.resolve("contestset.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userStatusFileExists(String handle) {
        return Files.exists(Path.of(FileUtils.UserStorageDir));
    }
}
