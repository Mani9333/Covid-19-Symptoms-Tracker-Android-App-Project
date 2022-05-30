package com.example.mcassignment;

public class Symptom {

    private String text;
    private String value;
    private float rating;

    public Symptom(String text, String value) {
        this.text = text;
        this.value = value;
        this.rating = 0;
    }

    public Symptom(String text, String value, float rating) {
        this.text = text;
        this.value = value;
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public float getRating() {
        return rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return text;
    }
}

