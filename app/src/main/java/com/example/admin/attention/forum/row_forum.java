package com.example.admin.attention.forum;

/**
 * Created by ADMIN on 3/31/2018.
 */

public class row_forum {

    private String question;
    private String answer;
    private String uid;

    public row_forum(){}

    public row_forum(String question, String answer, String uid) {
        this.question = question;
        this.answer = answer;
        this.uid = uid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
