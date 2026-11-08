package com.azathorpe.entities;

public class Submission {
    public enum Verdict {
        OK,
        PARTIAL,
        COMPILATION_ERROR,
        RUNTIME_ERROR,
        WRONG_ANSWER,
        PRESENTATION_ERROR,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        IDLENESS_LIMIT_EXCEEDED,
        SECURITY_VIOLATED,
        CRASHED,
        INPUT_PREPARATION_CRASHED,
        CHALLENGED,
        SKIPPED,
        TESTING,
        REJECTED
    }

    public enum Testset {
        SAMPLES,
        PRETESTS,
        TESTS,
        TESTS1,
        TESTS2,
        TESTS3,
        TESTS4,
        TESTS5,
        TESTS6,
        TESTS7,
        TESTS8,
        TESTS9
    }

    int id;
    int contestId;
    int creationTimeSeconds;
    int relativeTimeSeconds;
    Problem problem;
    User author;
    String programmingLanguage;
    Verdict verdict;
    Testset testset;
    int passedTestCount;
    int timeConsumedMillis;
    int memoryConsumedBytes;
    float points;

    public Submission() {
    }

    public Submission(int id, int contestId, int creationTimeSeconds, int relativeTimeSeconds, Problem problem, User author, String programmingLanguage, Verdict verdict, Testset testset, int passedTestCount, int timeConsumedMillis, int memoryConsumedBytes, float points) {
        this.id = id;
        this.contestId = contestId;
        this.creationTimeSeconds = creationTimeSeconds;
        this.relativeTimeSeconds = relativeTimeSeconds;
        this.problem = problem;
        this.author = author;
        this.programmingLanguage = programmingLanguage;
        this.verdict = verdict;
        this.testset = testset;
        this.passedTestCount = passedTestCount;
        this.timeConsumedMillis = timeConsumedMillis;
        this.memoryConsumedBytes = memoryConsumedBytes;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public int getCreationTimeSeconds() {
        return creationTimeSeconds;
    }

    public void setCreationTimeSeconds(int creationTimeSeconds) {
        this.creationTimeSeconds = creationTimeSeconds;
    }

    public int getRelativeTimeSeconds() {
        return relativeTimeSeconds;
    }

    public void setRelativeTimeSeconds(int relativeTimeSeconds) {
        this.relativeTimeSeconds = relativeTimeSeconds;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public void setVerdict(Verdict verdict) {
        this.verdict = verdict;
    }

    public Testset getTestset() {
        return testset;
    }

    public void setTestset(Testset testset) {
        this.testset = testset;
    }

    public int getPassedTestCount() {
        return passedTestCount;
    }

    public void setPassedTestCount(int passedTestCount) {
        this.passedTestCount = passedTestCount;
    }

    public int getTimeConsumedMillis() {
        return timeConsumedMillis;
    }

    public void setTimeConsumedMillis(int timeConsumedMillis) {
        this.timeConsumedMillis = timeConsumedMillis;
    }

    public int getMemoryConsumedBytes() {
        return memoryConsumedBytes;
    }

    public void setMemoryConsumedBytes(int memoryConsumedBytes) {
        this.memoryConsumedBytes = memoryConsumedBytes;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }
}