package com.example.trialstuff03_11_2021;

public class Binomial_Trial extends Trial {

    // passes/fails count
    private int numPasses;
    private int numFails;

    // constructor
    public Binomial_Trial(int numPasses, int numFails) {
        this.numPasses = numPasses;
        this.numFails = numFails;
    }

    // functions that add one pass/fail when run, used bc storyboard indicates this format
    // TODO: to be run every time the plus/minus button is clicked in the view
    public void addOnePass() {
        numPasses++;
    }

    public void addOneFail() {
        numFails++;
    }

    // getters
    public int getNumPasses() {
        return numPasses;
    }
    public int getNumFails() {
        return numFails;
    }
}
