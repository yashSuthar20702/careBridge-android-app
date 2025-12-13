package com.example.carebridge.wear.models;

import androidx.annotation.NonNull;

/**
 * MealItem

 * Model class that represents a single meal
 * shown in the Meal Planner screen on Wear OS.
 */
public class MealItem {

    // Name of the meal (e.g., Breakfast, Lunch, Dinner)
    private final String name;

    // Time when the meal should be taken
    private final String time;

    // Description of the meal (food details)
    private final String description;

    /**
     * Constructor

     * Creates an immutable MealItem object
     * with name, time, and description.
     */
    public MealItem(String name, String time, String description) {
        this.name = name;
        this.time = time;
        this.description = description;
    }

    // Returns the meal name
    public String getName() {
        return name;
    }

    // Returns the meal time
    public String getTime() {
        return time;
    }

    // Returns the meal description
    public String getDescription() {
        return description;
    }

    /**
     * Converts the meal object into readable text.
     * Useful for logging and debugging.
     */
    @NonNull
    @Override
    public String toString() {
        return name + " (" + time + "): " + description;
    }
}