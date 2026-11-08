package com.azathorpe.entities;

import java.util.Objects;

public class ProblemStatistics {
    int contestId;
    String index;
    int solvedCount;

    //组合键
    String combinationKey;

    public ProblemStatistics() {
    }

    public ProblemStatistics(int contestId, String index, int solvedCount) {
        this.contestId = contestId;
        this.index = index;
        this.solvedCount = solvedCount;
        this.combinationKey = contestId + "-" + index;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProblemStatistics that = (ProblemStatistics) o;
        return Objects.equals(combinationKey, that.combinationKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(combinationKey);
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getSolvedCount() {
        return solvedCount;
    }

    public void setSolvedCount(int solvedCount) {
        this.solvedCount = solvedCount;
    }
}
