package com.azathorpe.utils.cfetcher;

import com.azathorpe.entities.Message;
import com.azathorpe.utils.fileSystem.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class CFFetcher {
    private static final Logger log = LoggerFactory.getLogger(CFFetcher.class);
    /**
     * 我们需要确保不要太频繁访问Codeforces的API
     * 至少当前的时间戳距离上一次有两秒以上
     */
    static long lastFetchTimestamp = System.currentTimeMillis();

    /**
     * 是否能够进行fetch操作
     * 可以的话，更新lastFetchTimestamp
     *
     * @return 是否可以进行fetch操作
     */
    public static boolean canFetch() {
        long currentTimestamp = System.currentTimeMillis();
        if ((currentTimestamp - lastFetchTimestamp) >= 2000) {
            lastFetchTimestamp = currentTimestamp;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 等待直到可以进行fetch操作
     * 如果距离上一次fetch操作不足两秒，则等待直到两秒过去
     * 这个方法会阻塞当前线程，直到可以进行fetch操作
     */
    public static void waitForNextFetch() {
        long currentTimestamp = System.currentTimeMillis();
        long timeSinceLastFetch = currentTimestamp - lastFetchTimestamp;
        if (timeSinceLastFetch < 2000) {
            try {
                Thread.sleep(2000 - timeSinceLastFetch);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastFetchTimestamp = System.currentTimeMillis();
    }

    /**
     * 异步地获取Codeforces的题目列表
     * 这个方法会在后台线程中执行，不会阻塞主线程
     * 你可以通过返回的CompletableFuture对象来处理结果或异常
     *
     * @return 一个CompletableFuture对象，表示异步操作的结果
     */
    public static CompletableFuture<Void> fetchProblems() {
        return CompletableFuture.runAsync(() -> {
            // 等待直到可以进行fetch操作
            waitForNextFetch();
            try {
                Message message = CFSender.queryProblems();
                if (message.getStatus().equals("OK")) {
                    // 存储到本地文件
                    FileUtils.saveProblemSet(message.getResult());
                } else {
                    log.error("Failed to fetch problems: {}", message.getComment());
                }
            } catch (Exception e) {
                log.error("Error fetching problems: ", e);
            }
        });
    }

    public static CompletableFuture<Void> fetchUserStatus(String handle) {
        return CompletableFuture.runAsync(() -> {
            // 等待直到可以进行fetch操作
            waitForNextFetch();
            try {
                Message message = CFSender.queryStatus(handle);
                if (message.getStatus().equals("OK")) {
                    // 存储到本地文件
                    FileUtils.saveUserStatus(handle, message.getResult());
                } else {
                    log.error("Failed to fetch user status for {}: {}", handle, message.getComment());
                }
            } catch (Exception e) {
                log.error("Error fetching user status for {}: ", handle, e);
            }
        });
    }


}
