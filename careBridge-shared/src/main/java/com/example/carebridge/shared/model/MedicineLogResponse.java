package com.example.carebridge.shared.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MedicineLogResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("case_id")
    private String caseId;

    @SerializedName("count")
    private int count;

    @SerializedName("logs")
    private List<MedicineLog> logs;

    public boolean isSuccess() { return success; }
    public String getCaseId() { return caseId; }
    public int getCount() { return count; }
    public List<MedicineLog> getLogs() { return logs; }
}