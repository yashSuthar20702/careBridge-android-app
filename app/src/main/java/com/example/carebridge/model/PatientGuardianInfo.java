package com.example.carebridge.model;

/**
 * PatientGuardianInfo: Data model representing guardian information assigned to a patient
 * Contains guardian details, contact information, and assignment metadata
 */
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

    // ========== GETTER METHODS ==========

    /** Get unique assignment identifier for this guardian-patient relationship */
    public int getAssignment_id() { return assignment_id; }

    /** Get unique guardian identifier */
    public String getGuardian_id() { return guardian_id; }

    /** Get guardian's full name */
    public String getFull_name() { return full_name; }

    /** Get guardian's contact phone number */
    public String getPhone() { return phone; }

    /** Get guardian's email address */
    public String getEmail() { return email; }

    /** Get guardian's role (e.g., Family Member, Professional Caregiver) */
    public String getRole() { return role; }

    /** Get additional notes about the guardian or assignment */
    public String getNotes() { return notes; }

    /** Get guardian type classification */
    public String getType() { return type; }

    /** Get date when guardian was assigned to the patient */
    public String getAssigned_date() { return assigned_date; }

    /**
     * Returns string representation of guardian information for debugging
     * @return Formatted string containing key guardian details
     */
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