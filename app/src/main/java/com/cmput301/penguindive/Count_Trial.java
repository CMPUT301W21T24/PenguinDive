package com.example.trialstuff03_11_2021;

public class Count_Trial extends Trial{

    // variables
    private int count;  // the integer count of what we're trying to measure
    private int addedCount;  // TODO: the number of counts added/removed, to be collected from an converted edittext

    // constructor
    public Count_Trial(int count) {
        this.count = count;
    }

    // adds an integer to the count
    public void addCount() {
        count = count + addedCount;
    }

    // getters
    public int getCount() {
        return count;
    }
}

