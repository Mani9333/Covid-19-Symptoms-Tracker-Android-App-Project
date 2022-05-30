package com.example.mcassignment;

public class Measurement {

    private int redPixels;
    private long milliSeconds;

    public Measurement(int redPixels, long milliSeconds) {
        this.redPixels = redPixels;
        this.milliSeconds = milliSeconds;
    }

    public int getRedPixels() {
        return redPixels;
    }

    public long getMilliSeconds() {
        return milliSeconds;
    }

    public void setRedPixels(int redPixels) {
        this.redPixels = redPixels;
    }

    public void setMilliSeconds(long milliSeconds) {
        this.milliSeconds = milliSeconds;
    }
}
