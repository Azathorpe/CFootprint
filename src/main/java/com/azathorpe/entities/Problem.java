package com.azathorpe.entities;

import java.util.List;
import java.util.Objects;

public class Problem {
    int contestId;
    String index;
    String problemsetName;
    String name;
    String type;
    float points;
    int rating;
    List<String> tags;

    public Problem() {
    }

    public Problem(int contestId, String index, String problemsetName, String name, String type, float points, int rating, List<String> tags) {
        this.contestId = contestId;
        this.index = index;
        this.problemsetName = problemsetName;
        this.name = name;
        this.type = type;
        this.points = points;
        this.rating = rating;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return contestId == problem.contestId && Objects.equals(index, problem.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contestId, index);
    }

    @Override
    public String toString() {
        return contestId + " " + index + " " + problemsetName + " " + name + " " + type + " " + points + " " + rating + " " + tags;
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

    public String getProblemsetName() {
        return problemsetName;
    }

    public void setProblemsetName(String problemsetName) {
        this.problemsetName = problemsetName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
