package com.cmput301.penguindive;

import java.io.Serializable;

public class Non_Negative_Integer_Counts_Trial implements Serializable {

    //referenced
    //https://developer.android.com/reference/android/widget/TextView.html#attr_android:digits
    // for making non negative integer


    // variables
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

    // setters
    public int setNonNegativeInteger() { return nonNegativeInteger; }

}
