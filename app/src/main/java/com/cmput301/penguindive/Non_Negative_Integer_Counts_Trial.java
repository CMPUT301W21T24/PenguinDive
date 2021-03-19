package com.example.trialstuff03_11_2021;

public class Non_Negative_Integer_Counts_Trial extends Trial{

    // variables
    // TODO: non-negative integer should be enforced non negative in the layout file, check added in constructor just in case
    private int nonNegativeInteger;

    // constructor with check
    public Non_Negative_Integer_Counts_Trial(int nonNegativeInteger) {
        this.nonNegativeInteger = nonNegativeInteger;

        // throw generic exception if nonNegativeInteger is negative
        try {
            if (this.nonNegativeInteger < 0) {
                throw new Exception("Non-Negative integer is negative!");
            }
        }
        catch (Exception negativeException) {
            negativeException.printStackTrace();  // print stack trace if an exception happens
        }
    }

    // getters
    public int getNonNegativeInteger() {
        return nonNegativeInteger;
    }

}
