package com.cmput301.penguindive;

public class Count_Trial {

    // variables
    private int count;  // the integer count of what we're trying to measure

    // constructor
    public Count_Trial() {
        this.count = 0;  // should always start at zero
    }

    // incrememnts count by 1
    public void incrementCount() {
        count++;
    }

    // decrement count by 1
    public void decrementCount() {
        count--;
    }

    // getters
    public int getCount() {
        return count;
    }



}

