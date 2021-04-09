package com.cmput301.penguindive;

/**
 * This class represents an Answer object
 */
public class Answer {

    private String answerText;
    private String answerUserID;

    public Answer(String answerText, String answerUserID ) {
        this.answerText = answerText;
        this.answerUserID = answerUserID;
    }

    public String getAnswerUserID() {
        return answerUserID;
    }

    public String getAnswerText() {
        return answerText;
    }


}
