package com.example.carebridge.model;

import java.io.Serializable;

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

    // Getters and Setters
    public String getPatient_id() { return patient_id; }
    public void setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContact_number() { return contact_number; }
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAssigned_date() { return assigned_date; }
    public void setAssigned_date(String assigned_date) { this.assigned_date = assigned_date; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

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
