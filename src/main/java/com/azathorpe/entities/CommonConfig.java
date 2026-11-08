package com.azathorpe.entities;

public class CommonConfig {
    long problemLastFetchTimeStamp;
    long contestLastFetchTimeStamp;

    public CommonConfig() {
    }

    public CommonConfig(long problemLastFetchTimeStamp, long contestLastFetchTimeStamp) {
        this.problemLastFetchTimeStamp = problemLastFetchTimeStamp;
        this.contestLastFetchTimeStamp = contestLastFetchTimeStamp;
    }

    public long getProblemLastFetchTimeStamp() {
        return problemLastFetchTimeStamp;
    }

    public void setProblemLastFetchTimeStamp(long problemLastFetchTimeStamp) {
        this.problemLastFetchTimeStamp = problemLastFetchTimeStamp;
    }

    public long getContestLastFetchTimeStamp() {
        return contestLastFetchTimeStamp;
    }

    public void setContestLastFetchTimeStamp(long contestLastFetchTimeStamp) {
        this.contestLastFetchTimeStamp = contestLastFetchTimeStamp;
    }
}
