package com.example.carebridge.model;

import android.util.Log;
import java.io.Serializable;

public class User implements Serializable {
    private static final String TAG = "UserModel";

    private int id;
    private String username;
    private String role;
    private String referenceId;
    private String createdAt;

    private PatientInfo patientInfo; // <-- store linked_data here

    public int getId() { return id; }
    public void setId(int id) { this.id = id; Log.d(TAG, "setId: " + id); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; Log.d(TAG, "setUsername: " + username); }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; Log.d(TAG, "setRole: " + role); }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public PatientInfo getPatientInfo() { return patientInfo; }
    public void setPatientInfo(PatientInfo patientInfo) {
        this.patientInfo = patientInfo;
        Log.d(TAG, "PatientInfo set: " + patientInfo);
    }
}
