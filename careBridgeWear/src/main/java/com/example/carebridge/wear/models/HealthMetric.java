package com.example.carebridge.wear.models;

public class HealthMetric {
    private String id;
    private String label;
    private String value;
    private String unit;
    private String description;
    private int iconRes;
    private int colorRes;
    private int bgGradientStart;
    private int bgGradientEnd;
    private String detailActivity;

    public HealthMetric(String id, String label, String value, String unit,
                        String description, int iconRes, int colorRes,
                        int bgGradientStart, int bgGradientEnd, String detailActivity) {
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

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getIconRes() { return iconRes; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }

    public int getColorRes() { return colorRes; }
    public void setColorRes(int colorRes) { this.colorRes = colorRes; }

    public int getBgGradientStart() { return bgGradientStart; }
    public void setBgGradientStart(int bgGradientStart) { this.bgGradientStart = bgGradientStart; }

    public int getBgGradientEnd() { return bgGradientEnd; }
    public void setBgGradientEnd(int bgGradientEnd) { this.bgGradientEnd = bgGradientEnd; }

    public String getDetailActivity() { return detailActivity; }
    public void setDetailActivity(String detailActivity) { this.detailActivity = detailActivity; }
}