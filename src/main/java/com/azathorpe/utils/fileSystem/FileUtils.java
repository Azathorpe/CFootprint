package com.azathorpe.utils.fileSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
