package com.cmput301.penguindive;

import java.io.Serializable;

public class Trial implements Serializable {

    // variables
    private int numberOfTrials;  // incremented whenever a new trial is made

    // getters
    public int getNumberOfTrials() {
        return numberOfTrials;
    }
}
