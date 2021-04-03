package com.cmput301.penguindive;

import java.io.Serializable;

public class Measurement_Trial implements Serializable {

    // variables
    // TODO: Get this from the view/edittext
    private double measurement;

    // constructor
    public Measurement_Trial(double measurement) {
        this.measurement = measurement;
    }

    // getters
    public double getMeasurement() {
        return measurement;
    }
}
