package com.example.carebridge.wear.models;

public class Medicine {
    private String name;
    private String dosage;
    private String time;
    private boolean taken;

    public Medicine(String name, String dosage, String time, boolean taken) {
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.taken = taken;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) { this.taken = taken; }
}