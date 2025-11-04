package com.example.carebridge.model;

import java.util.List;

public class Prescription {
    private int prescription_id;
    private String case_id;
    private String doctor_name;
    private String created_at;
    private List<Medication> medicines;

    public int getPrescription_id() { return prescription_id; }
    public void setPrescription_id(int prescription_id) { this.prescription_id = prescription_id; }

    public String getCase_id() { return case_id; }
    public void setCase_id(String case_id) { this.case_id = case_id; }

    public String getDoctor_name() { return doctor_name; }
    public void setDoctor_name(String doctor_name) { this.doctor_name = doctor_name; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public List<Medication> getMedicines() { return medicines; }
    public void setMedicines(List<Medication> medicines) { this.medicines = medicines; }
}
