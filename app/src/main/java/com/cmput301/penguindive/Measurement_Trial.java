package com.cmput301.penguindive;

import java.io.Serializable;

public class Measurement_Trial implements Serializable {

    // variables
    private double measurement;
    private String measurementName;

    // constructor
    public Measurement_Trial(double measurement, String measurementName) {
        this.measurement = measurement;
        this.measurementName = measurementName;
    }

    // getters
    public double getMeasurement() {
        return measurement;
    }
    public String getMeasurementName() {return measurementName; }
}
