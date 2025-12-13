package com.example.carebridge.wear.models;

/**
 * HealthInfo model class

 * Represents a single health information item
 * displayed in the Health Info screen on Wear OS.
 * Example: Age, Blood Group, Address, etc.
 */
public class HealthInfo {

    // Label shown on the UI (e.g., "Blood Group")
    private final String label;

    // Value corresponding to the label (e.g., "B+")
    private final String value;

    // Icon resource ID used for visual representation
    private final int iconRes;

    /**
     * Constructor to create a HealthInfo object
     *
     * @param label   Title of the health info
     * @param value   Value of the health info
     * @param iconRes Drawable resource for the icon
     */
    public HealthInfo(String label, String value, int iconRes) {
        this.label = label;
        this.value = value;
        this.iconRes = iconRes;
    }

    // Returns the health info label
    public String getLabel() {
        return label;
    }

    // Returns the health info value
    public String getValue() {
        return value;
    }

    // Returns the icon resource ID
    public int getIconRes() {
        return iconRes;
    }
}