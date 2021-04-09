package com.cmput301.penguindive;

public class Question {

    private String question;
    private String questionId;
    private String questionTitle;


    public Question(String question, String questionId, String questionTitle) {
        this.question = question;
        this.questionId = questionId;
        this.questionTitle = questionTitle;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }
}

