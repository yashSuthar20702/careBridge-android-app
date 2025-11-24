package com.example.carebridge.wear.models;

public class HealthInfo {
    private String label;
    private String value;
    private int iconRes;

    public HealthInfo(String label, String value, int iconRes) {
        this.label = label;
        this.value = value;
        this.iconRes = iconRes;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public int getIconRes() { return iconRes; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }
}