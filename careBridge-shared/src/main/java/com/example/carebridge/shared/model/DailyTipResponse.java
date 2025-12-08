package com.example.carebridge.shared.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class DailyTipResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("type")
    private String type; // "tip" or "video"

    @SerializedName("data")
    private JsonElement data; // Single field, parsed dynamically

    public String getStatus() { return status; }
    public String getType() { return type; }

    public boolean isSuccess() { return "success".equalsIgnoreCase(status); }

    // Parse tips if type = tip
    public List<Tip> getTipList() {
        if (!"tip".equalsIgnoreCase(type) || data == null) return null;
        return new Gson().fromJson(data, new TypeToken<List<Tip>>(){}.getType());
    }

    // Parse video if type = video
    public Video getVideo() {
        if (!"video".equalsIgnoreCase(type) || data == null) return null;
        return new Gson().fromJson(data, Video.class);
    }
}
