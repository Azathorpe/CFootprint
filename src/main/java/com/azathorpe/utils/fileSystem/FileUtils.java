package com.azathorpe.utils.fileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static final String userDir = System.getProperty("user.home");
    public static final Path userPath = Path.of(System.getProperty("user.home"));
    public static final String FileSystemBaseDir = Paths.get(userDir).resolve(".cfp").toString();
    public static final Path FileSystemBasePath = Paths.get(userDir).resolve(".cfp");
    public static final String ProblemStorageDir = Paths.get(FileSystemBaseDir).resolve("problems").toString();
    public static final Path ProblemStoragePath = Paths.get(FileSystemBaseDir).resolve("problems");
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
            Files.createDirectories(UserStoragePath);
            log.info("Checking Over......");
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
            FileWriter fw = new FileWriter(UserStoragePath.resolve(handle).toFile());
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readUserStatus(String handle) {
        try {
            return Files.readString(UserStoragePath.resolve(handle));
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
}
