package com.cmput301.penguindive;

/**
 * This class represents a binomial trial object
 * Has a value of pass or fail
 */
public class Binomial_Trial {

    private boolean pass;

    // constructor
    public Binomial_Trial(boolean pass) {
        this.pass = pass;
    }

    public boolean getPass() {
        return pass;
    }
}
