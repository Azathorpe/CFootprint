package com.azathorpe.utils.cfetcher;

public class CFFetcher {
    /**
     * 我们需要确保不要太频繁访问Codeforces的API
     * 至少当前的时间戳距离上一次有两秒以上
     */
    long lastFetchTimestamp = System.currentTimeMillis();

    /**
     * 是否能够进行fetch操作
     * 可以的话，更新lastFetchTimestamp
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


}
