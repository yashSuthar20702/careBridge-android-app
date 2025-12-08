package com.example.carebridge.shared.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("tip_id")
    private String tipId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String url;  // YouTube link

    @SerializedName("type")
    private String type;

    @SerializedName("category")
    private String category;

    public String getTipId() { return tipId; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
}
