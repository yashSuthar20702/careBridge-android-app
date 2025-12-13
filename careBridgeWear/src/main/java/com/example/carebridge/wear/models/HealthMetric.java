package com.example.carebridge.wear.models;

/**
 * HealthMetric

 * Model class that represents one health metric card
 * shown on the Wear OS home or health monitor screen.

 * Examples:
 * - Heart Rate
 * - Steps
 * - Blood Oxygen
 */
public class HealthMetric {

    // Unique identifier for the metric (used internally)
    private final String id;

    // Display name shown on the UI (e.g., "Heart Rate")
    private final String label;

    // Current value of the metric (e.g., "72")
    private String value;

    // Unit of measurement (e.g., "bpm", "steps", "%")
    private final String unit;

    // Short description shown below the value
    private final String description;

    // Icon resource used for this metric
    private final int iconRes;

    // Primary color resource for icon and highlights
    private final int colorRes;

    // Gradient background start color
    private final int bgGradientStart;

    // Gradient background end color
    private final int bgGradientEnd;

    // Activity class opened when the metric is clicked
    private final Class<?> detailActivity;

    /**
     * Constructor

     * Creates a complete HealthMetric object with all UI
     * and navigation information in one place.
     */
    public HealthMetric(
            String id,
            String label,
            String value,
            String unit,
            String description,
            int iconRes,
            int colorRes,
            int bgGradientStart,
            int bgGradientEnd,
            Class<?> detailActivity
    ) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.unit = unit;
        this.description = description;
        this.iconRes = iconRes;
        this.colorRes = colorRes;
        this.bgGradientStart = bgGradientStart;
        this.bgGradientEnd = bgGradientEnd;
        this.detailActivity = detailActivity;
    }

    // Returns the unique metric ID
    public String getId() {
        return id;
    }

    // Returns the metric display label
    public String getLabel() {
        return label;
    }

    // Returns the current metric value
    public String getValue() {
        return value;
    }

    // Returns the unit of measurement
    public String getUnit() {
        return unit;
    }

    // Returns the description text
    public String getDescription() {
        return description;
    }

    // Returns the icon resource ID
    public int getIconRes() {
        return iconRes;
    }

    // Returns the main color resource ID
    public int getColorRes() {
        return colorRes;
    }

    // Returns gradient start color resource
    public int getBgGradientStart() {
        return bgGradientStart;
    }

    // Returns gradient end color resource
    public int getBgGradientEnd() {
        return bgGradientEnd;
    }

    // Returns the Activity class for detailed view
    public Class<?> getDetailActivity() {
        return detailActivity;
    }

    /**
     * Updates the metric value dynamically
     * Used when sensor or simulated data changes.
     */
    public void setValue(String value) {
        this.value = value;
    }
}