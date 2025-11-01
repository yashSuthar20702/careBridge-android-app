package com.example.carebridge.model;

import java.io.Serializable;

/**
 * AssignedPatientInfo: Data model representing a patient assigned to a guardian
 * Contains personal, contact, and assignment information for patient management
 */
public class AssignedPatientInfo implements Serializable {
    private String patient_id;
    private String full_name;
    private String gender;
    private int age;
    private String status;
    private String contact_number;
    private String email;
    private String assigned_date;
    private String role;
    private String notes;

    // ========== GETTER METHODS ==========

    /** Get unique patient identifier */
    public String getPatient_id() { return patient_id; }

    /** Get patient's full name */
    public String getFull_name() { return full_name; }

    /** Get patient's gender */
    public String getGender() { return gender; }

    /** Get patient's age */
    public int getAge() { return age; }

    /** Get patient's current status (Active/Inactive) */
    public String getStatus() { return status; }

    /** Get patient's contact phone number */
    public String getContact_number() { return contact_number; }

    /** Get patient's email address */
    public String getEmail() { return email; }

    /** Get date when patient was assigned to guardian */
    public String getAssigned_date() { return assigned_date; }

    /** Get patient's role or type */
    public String getRole() { return role; }

    /** Get additional notes about the patient */
    public String getNotes() { return notes; }

    // ========== SETTER METHODS ==========

    /** Set unique patient identifier */
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    /** Set patient's full name */
    public void setFull_name(String full_name) { this.full_name = full_name; }

    /** Set patient's gender */
    public void setGender(String gender) { this.gender = gender; }

    /** Set patient's age */
    public void setAge(int age) { this.age = age; }

    /** Set patient's current status */
    public void setStatus(String status) { this.status = status; }

    /** Set patient's contact phone number */
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    /** Set patient's email address */
    public void setEmail(String email) { this.email = email; }

    /** Set assignment date */
    public void setAssigned_date(String assigned_date) { this.assigned_date = assigned_date; }

    /** Set patient's role or type */
    public void setRole(String role) { this.role = role; }

    /** Set additional patient notes */
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Returns string representation of patient information
     * @return Formatted string with all patient details
     */
    @Override
    public String toString() {
        return "AssignedPatientInfo{" +
                "patient_id='" + patient_id + '\'' +
                ", full_name='" + full_name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", status='" + status + '\'' +
                ", contact_number='" + contact_number + '\'' +
                ", email='" + email + '\'' +
                ", assigned_date='" + assigned_date + '\'' +
                ", role='" + role + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}