package com.example.carebridge.wear.models;

public class MealItem {

    private String name;
    private String time;
    private String description;

    public MealItem(String name, String time, String description) {
        this.name = name;
        this.time = time;
        this.description = description;
    }

    public String getName() { return name; }
    public String getTime() { return time; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = name; }
    public void setTime(String time) { this.time = time; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name + " (" + time + "): " + description;
    }
}
