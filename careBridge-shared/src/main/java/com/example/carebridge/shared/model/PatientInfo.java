package com.example.carebridge.shared.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class PatientInfo implements Serializable {

    @SerializedName("case_id")
    private String caseId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("dob")
    private String dob;

    @SerializedName("gender")
    private String gender;

    @SerializedName("address")
    private String address;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("email")
    private String email;

    // API uses "Status" with capital S
    @SerializedName("Status")
    private String status;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("general_info")
    private String generalInfo;

    @SerializedName("blood_group")
    private String bloodGroup;

    @SerializedName("height_cm")
    private String heightCm;

    @SerializedName("weight_kg")
    private String weightKg;

    @SerializedName("past_surgeries")
    private String pastSurgeries;

    @SerializedName("current_symptoms")
    private String currentSymptoms;

    @SerializedName("allergies")
    private List<String> allergies;

    @SerializedName("medical_conditions")
    private List<String> medicalConditions;

    private int totalMedicines = 0;
    private int takenMedicines = 0;

    // Constructors
    public PatientInfo() {}

    public PatientInfo(String caseId, String fullName) {
        this.caseId = caseId;
        this.fullName = fullName;
    }

    // Getters and Setters
    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getGeneralInfo() { return generalInfo; }
    public void setGeneralInfo(String generalInfo) { this.generalInfo = generalInfo; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getHeightCm() { return heightCm; }
    public void setHeightCm(String heightCm) { this.heightCm = heightCm; }

    public String getWeightKg() { return weightKg; }
    public void setWeightKg(String weightKg) { this.weightKg = weightKg; }

    public String getPastSurgeries() { return pastSurgeries; }
    public void setPastSurgeries(String pastSurgeries) { this.pastSurgeries = pastSurgeries; }

    public String getCurrentSymptoms() { return currentSymptoms; }
    public void setCurrentSymptoms(String currentSymptoms) { this.currentSymptoms = currentSymptoms; }

    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    public List<String> getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(List<String> medicalConditions) { this.medicalConditions = medicalConditions; }

    public int getTotalMedicines() { return totalMedicines; }
    public void setTotalMedicines(int totalMedicines) { this.totalMedicines = totalMedicines; }

    public int getTakenMedicines() { return takenMedicines; }
    public void setTakenMedicines(int takenMedicines) { this.takenMedicines = takenMedicines; }

    public int getRemainingMedicines() { return totalMedicines - takenMedicines; }
}
