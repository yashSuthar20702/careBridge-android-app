package com.example.carebridge.model;

public class Medication {
    private String name;
    private String dosage;
    private String time;

    public Medication(String name, String dosage, String time) {
        this.name = name;
        this.dosage = dosage;
        this.time = time;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
