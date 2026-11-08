package com.azathorpe.entities;

public class Message {
    public String status = "";
    /**
     * 原始 JSON 字符串：problemset.problems 是对象，user.status 是数组
     */
    public String result = "";
    public String comment = "";

    public Message() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}