package com.example.mcassignment;

import java.util.ArrayList;
import java.util.List;

public class User {

    private static User user;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    List<Symptom> symptoms;
    private double respiratoryRate;
    private double heartRate;
    private LocData location;

    public User(String username, String firstname, String lastname, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.symptoms = getSymptomsList();
        this.location = new LocData();
    }

    public static User getInstance() {
        return user;
    }

    public static void setInstance(User user) {
        User.user = user;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public LocData getLocation() {
        return location;
    }

    public void setLocation(LocData location) {
        this.location = location;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public double getRespiratoryRate() {
        return respiratoryRate;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public void setRespiratoryRate(double respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }

    public List<Symptom> getSymptomsList() {

        List<Symptom> symptomsList = new ArrayList<>();

        symptomsList.add(new Symptom("Nausea", "nausea"));
        symptomsList.add(new Symptom("Headache", "headache"));
        symptomsList.add(new Symptom("Diarrhea", "diarrhea"));
        symptomsList.add(new Symptom("Soar Throat", "soar_throat"));
        symptomsList.add(new Symptom("Fever", "fever"));
        symptomsList.add(new Symptom("Muscle Ache", "muscle_ache"));
        symptomsList.add(new Symptom("Loss of Smell or Taste", "loss_smell_taste"));
        symptomsList.add(new Symptom("Cough", "cough"));
        symptomsList.add(new Symptom("Shortness of Breath", "shortness_breath"));
        symptomsList.add(new Symptom("Feeling Tired", "tired"));

        return symptomsList;
    }
}

