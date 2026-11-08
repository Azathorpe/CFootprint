package com.azathorpe.entities;

import java.util.List;

public class Problems {
    List<Problem> problems;
    List<ProblemStatistics> problemStatistics;

    public Problems() {
    }

    public Problems(List<Problem> problems, List<ProblemStatistics> problemStatistics) {
        this.problems = problems;
        this.problemStatistics = problemStatistics;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }

    public List<ProblemStatistics> getProblemStatistics() {
        return problemStatistics;
    }

    public void setProblemStatistics(List<ProblemStatistics> problemStatistics) {
        this.problemStatistics = problemStatistics;
    }
}
