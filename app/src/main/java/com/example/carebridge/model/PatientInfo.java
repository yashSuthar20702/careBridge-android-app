package com.example.carebridge.model;

import java.io.Serializable;
import java.util.List;

public class PatientInfo implements Serializable {
    private String case_id;
    private String full_name;
    private String dob;
    private String gender;
    private String address;
    private String contact_number;
    private String email;
    private String status;
    private String created_at;

    private String general_info;
    private String blood_group;
    private String height_cm;
    private String weight_kg;
    private String past_surgeries;
    private String current_symptoms;

    private List<String> allergies;
    private List<String> medical_conditions;

    // Getters and Setters
    public String getCase_id() { return case_id; }
    public void setCase_id(String case_id) { this.case_id = case_id; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact_number() { return contact_number; }
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getGeneral_info() { return general_info; }
    public void setGeneral_info(String general_info) { this.general_info = general_info; }

    public String getBlood_group() { return blood_group; }
    public void setBlood_group(String blood_group) { this.blood_group = blood_group; }

    public String getHeight_cm() { return height_cm; }
    public void setHeight_cm(String height_cm) { this.height_cm = height_cm; }

    public String getWeight_kg() { return weight_kg; }
    public void setWeight_kg(String weight_kg) { this.weight_kg = weight_kg; }

    public String getPast_surgeries() { return past_surgeries; }
    public void setPast_surgeries(String past_surgeries) { this.past_surgeries = past_surgeries; }

    public String getCurrent_symptoms() { return current_symptoms; }
    public void setCurrent_symptoms(String current_symptoms) { this.current_symptoms = current_symptoms; }

    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    public List<String> getMedical_conditions() { return medical_conditions; }
    public void setMedical_conditions(List<String> medical_conditions) { this.medical_conditions = medical_conditions; }
}
