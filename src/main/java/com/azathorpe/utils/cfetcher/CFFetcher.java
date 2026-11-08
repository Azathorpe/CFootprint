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
    long lastFetchTimestamp = System.currentTimeMillis();

    /**
     * 是否能够进行fetch操作
     * 可以的话，更新lastFetchTimestamp
     *
     * @return 是否可以进行fetch操作
     */
    public boolean canFetch() {
        long currentTimestamp = System.currentTimeMillis();
        if ((currentTimestamp - lastFetchTimestamp) >= 2000) {
            lastFetchTimestamp = currentTimestamp;
            return true;
        } else {
            return false;
        }
    }

    public void waitForNextFetch() {
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
    public CompletableFuture<Void> fetchProblems() {
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

    public CompletableFuture<Void> fetchUserStatus(String handle) {
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
