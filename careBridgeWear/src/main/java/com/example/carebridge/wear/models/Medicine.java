package com.example.carebridge.wear.models;

/**
 * Medicine

 * Model class representing a single medicine item
 * displayed on the Wear OS Medicine screen.

 * This class is immutable to ensure data consistency
 * across the application.
 */
public class Medicine {

    // Name of the medicine (e.g., Paracetamol)
    private final String name;

    // Dosage information (e.g., 500 mg)
    private final String dosage;

    // Time when the medicine should be taken
    private final String time;

    // Indicates whether the medicine has already been taken
    private final boolean taken;

    /**
     * Constructor

     * Creates an immutable Medicine object with all
     * required details.
     */
    public Medicine(String name, String dosage, String time, boolean taken) {
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.taken = taken;
    }

    /**
     * Returns the medicine name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the dosage information.
     * This method is intentionally provided for
     * adapters and future UI extensions.
     */
    @SuppressWarnings("unused")
    public String getDosage() {
        return dosage;
    }

    /**
     * Returns the scheduled time for the medicine.
     */
    public String getTime() {
        return time;
    }

    /**
     * Returns whether the medicine has been taken.
     * Used for UI state (opacity, indicators, etc.).
     */
    @SuppressWarnings("unused")
    public boolean isTaken() {
        return taken;
    }
}