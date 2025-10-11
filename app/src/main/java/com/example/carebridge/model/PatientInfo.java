package com.example.carebridge.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PatientInfo implements Serializable {

    @SerializedName("case_id")
    private String caseId = "";

    @SerializedName("full_name")
    private String fullName = "";

    @SerializedName("dob")
    private String dob = "";

    @SerializedName("gender")
    private String gender = "";

    @SerializedName("address")
    private String address = "";

    @SerializedName("contact_number")
    private String contactNumber = "";

    @SerializedName("email")
    private String email = "";

    @SerializedName("Status")
    private String status = "";

    @SerializedName("created_at")
    private String createdAt = "";

    // Default constructor
    public PatientInfo() {}

    // Getters and Setters
    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId != null ? caseId : ""; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName != null ? fullName : ""; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob != null ? dob : ""; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender != null ? gender : ""; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address != null ? address : ""; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber != null ? contactNumber : ""; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email : ""; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status != null ? status : ""; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt != null ? createdAt : ""; }

    @Override
    public String toString() {
        return "PatientInfo{" +
                "caseId='" + caseId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
