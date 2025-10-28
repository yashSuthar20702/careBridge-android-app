package com.example.carebridge.model;

public class PatientGuardianInfo {
    private int assignment_id;
    private String guardian_id;
    private String full_name;
    private String phone;
    private String email;
    private String role;
    private String notes;
    private String type;
    private String assigned_date;

    // Getters
    public int getAssignment_id() { return assignment_id; }
    public String getGuardian_id() { return guardian_id; }
    public String getFull_name() { return full_name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getNotes() { return notes; }
    public String getType() { return type; }
    public String getAssigned_date() { return assigned_date; }

    @Override
    public String toString() {
        return "PatientGuardianInfo{" +
                "guardian_id='" + guardian_id + '\'' +
                ", full_name='" + full_name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
