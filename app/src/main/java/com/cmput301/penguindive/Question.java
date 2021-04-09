package com.cmput301.penguindive;

/**
 * This class represents a quesiton object
 */
public class Question {

    private String question;
    private String questionId;
    private String questionTitle;
    private String questionUserId;


    public Question(String question, String questionId, String questionTitle, String question_user_id) {
        this.question = question;
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionUserId = question_user_id;
    }

    public String getQuestionUserId() {
        return questionUserId;
    }

    public void setQuestion_user_id(String question_user_id) {
        this.questionUserId = question_user_id;
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

