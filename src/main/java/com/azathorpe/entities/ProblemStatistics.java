package com.azathorpe.entities;

import java.util.Objects;

public class ProblemStatistics {
    int contestId;
    String index;
    int solvedCount;

    public ProblemStatistics() {
    }

    public ProblemStatistics(int contestId, String index, int solvedCount) {
        this.contestId = contestId;
        this.index = index;
        this.solvedCount = solvedCount;
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
