package com.example.carebridge.shared.model;

import com.google.gson.annotations.SerializedName;

public class Tip {

    @SerializedName("tip_id")
    private String tipId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type;

    @SerializedName("category")
    private String category;

    @SerializedName("active_status")
    private String activeStatus;

    @SerializedName("show_date")
    private String showDate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getTipId() { return tipId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getCategory() { return category; }
}
