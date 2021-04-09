package com.cmput301.penguindive;

import junit.framework.TestCase;

import org.junit.Assert;

public class AnswerTest extends TestCase {

    public void testGetAnswerText() {
        Answer ans = new Answer("answer_to_question","1234");
        String ans_text = ans.getAnswerText();
        String userid = ans.getAnswerUserID();
        Assert.assertEquals("answer_to_question",ans_text);
        Assert.assertEquals("1234",userid);


    }
}