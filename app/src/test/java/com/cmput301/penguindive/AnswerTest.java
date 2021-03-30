package com.cmput301.penguindive;

import junit.framework.TestCase;

import org.junit.Assert;

public class AnswerTest extends TestCase {

    public void testGetAnswerText() {
        Answer ans = new Answer("answer_to_question");
        String ans_text = ans.getAnswerText();
        Assert.assertEquals("answer_to_question",ans_text);

    }
}