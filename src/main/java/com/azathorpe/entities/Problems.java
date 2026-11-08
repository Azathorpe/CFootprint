package com.azathorpe.entities;

import java.util.List;

public class Problems {
    List<Problem> problems;

    public Problems() {
    }

    public Problems(List<Problem> problems) {
        this.problems = problems;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }
}
