package com.cmput301.penguindive;

import junit.framework.TestCase;

import org.junit.Assert;

public class QuestionTest extends TestCase {

    public void testGetQuestion() {
        Question ques = new Question("Question", "ID","Title");
        String result = ques.getQuestion();
        Assert.assertEquals("Question",result);
    }

    public void testSetQuestion() {
        Question ques = new Question("Question", "ID","Title");
        ques.setQuestion("newQ");
        Assert.assertEquals("newQ",ques.getQuestion());
    }

    public void testGetQuestionId() {
        Question ques = new Question("Question", "ID","Title");
        String result = ques.getQuestionId();
        Assert.assertEquals("ID",result);
    }

    public void testSetQuestionId() {
        Question ques = new Question("Question", "ID","Title");
        ques.setQuestionId("newId");
        Assert.assertEquals("newId",ques.getQuestionId());
    }

    public void testGetQuestionTitle() {
        Question ques = new Question("Question", "ID","Title");
        String result = ques.getQuestionTitle();
        Assert.assertEquals("Title",result);
    }

    public void testSetQuestionTitle() {
        Question ques = new Question("Question", "ID","Title");
        ques.setQuestionTitle("newTitle");
        Assert.assertEquals("newTitle",ques.getQuestionTitle());
    }
}