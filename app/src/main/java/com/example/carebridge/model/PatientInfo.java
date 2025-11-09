package com.example.carebridge.model;

import java.io.Serializable;
import java.util.List;

/**
 * PatientInfo: Comprehensive data model representing patient medical and personal information
 * Contains demographic data, medical history, contact details, and health metrics
 */
public class PatientInfo implements Serializable {
    // Basic Identification Fields
    private String case_id;
    private String full_name;
    private String dob;
    private String gender;
    private String address;
    private String contact_number;
    private String email;
    private String status;
    private String created_at;

    // Medical Information Fields
    private String general_info;
    private String blood_group;
    private String height_cm;
    private String weight_kg;
    private String past_surgeries;
    private String current_symptoms;

    private int totalMedicines = 0;
    private int takenMedicines = 0;

    // List-based Medical Data
    private List<String> allergies;
    private List<String> medical_conditions;

    // ========== GETTER METHODS ==========

    /** Get unique case identifier for the patient */
    public String getCase_id() { return case_id; }

    /** Get patient's full legal name */
    public String getFull_name() { return full_name; }

    /** Get patient's date of birth (YYYY-MM-DD format) */
    public String getDob() { return dob; }

    /** Get patient's gender */
    public String getGender() { return gender; }

    /** Get patient's residential address */
    public String getAddress() { return address; }

    /** Get patient's primary contact phone number */
    public String getContact_number() { return contact_number; }

    /** Get patient's email address */
    public String getEmail() { return email; }

    /** Get patient's current health status (e.g., Stable, Critical) */
    public String getStatus() { return status; }

    /** Get record creation timestamp */
    public String getCreated_at() { return created_at; }

    /** Get general medical notes and observations */
    public String getGeneral_info() { return general_info; }

    /** Get patient's blood group type */
    public String getBlood_group() { return blood_group; }

    /** Get patient's height in centimeters */
    public String getHeight_cm() { return height_cm; }

    /** Get patient's weight in kilograms */
    public String getWeight_kg() { return weight_kg; }

    /** Get history of past surgical procedures */
    public String getPast_surgeries() { return past_surgeries; }

    /** Get description of current presenting symptoms */
    public String getCurrent_symptoms() { return current_symptoms; }

    /** Get list of patient's known allergies */
    public List<String> getAllergies() { return allergies; }

    /** Get list of patient's medical conditions/diagnoses */
    public List<String> getMedical_conditions() { return medical_conditions; }

    // ========== SETTER METHODS ==========

    /** Set unique case identifier */
    public void setCase_id(String case_id) { this.case_id = case_id; }

    /** Set patient's full name */
    public void setFull_name(String full_name) { this.full_name = full_name; }

    /** Set patient's date of birth */
    public void setDob(String dob) { this.dob = dob; }

    /** Set patient's gender */
    public void setGender(String gender) { this.gender = gender; }

    /** Set patient's residential address */
    public void setAddress(String address) { this.address = address; }

    /** Set patient's contact number */
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    /** Set patient's email address */
    public void setEmail(String email) { this.email = email; }

    /** Set patient's current health status */
    public void setStatus(String status) { this.status = status; }

    /** Set record creation timestamp */
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    /** Set general medical notes */
    public void setGeneral_info(String general_info) { this.general_info = general_info; }

    /** Set patient's blood group */
    public void setBlood_group(String blood_group) { this.blood_group = blood_group; }

    /** Set patient's height in centimeters */
    public void setHeight_cm(String height_cm) { this.height_cm = height_cm; }

    /** Set patient's weight in kilograms */
    public void setWeight_kg(String weight_kg) { this.weight_kg = weight_kg; }

    /** Set history of past surgeries */
    public void setPast_surgeries(String past_surgeries) { this.past_surgeries = past_surgeries; }

    /** Set current symptoms description */
    public void setCurrent_symptoms(String current_symptoms) { this.current_symptoms = current_symptoms; }

    /** Set list of patient allergies */
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    /** Set list of medical conditions */
    public void setMedical_conditions(List<String> medical_conditions) { this.medical_conditions = medical_conditions; }

    public int getTotalMedicines() { return totalMedicines; }
    public void setTotalMedicines(int totalMedicines) { this.totalMedicines = totalMedicines; }

    public int getTakenMedicines() { return takenMedicines; }
    public void setTakenMedicines(int takenMedicines) { this.takenMedicines = takenMedicines; }

    public int getRemainingMedicines() { return totalMedicines - takenMedicines; }

}