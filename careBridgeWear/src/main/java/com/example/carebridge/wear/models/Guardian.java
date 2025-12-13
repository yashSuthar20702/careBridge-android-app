package com.example.carebridge.wear.models;

/**
 * Guardian model class

 * Represents a guardian or caregiver linked to a patient.
 * This class is used to transfer guardian data to UI components.
 */
public class Guardian {

    // Guardian's full name
    private final String name;

    // Guardian category (Family, Medical, Caretaker, etc.)
    private final String type;

    // Relationship with the patient
    private final String relation;

    // Guardian contact phone number
    private final String phone;

    /**
     * Constructor used to create a Guardian object
     */
    public Guardian(String name, String type, String relation, String phone) {
        this.name = name;
        this.type = type;
        this.relation = relation;
        this.phone = phone;
    }

    // Returns guardian name
    public String getName() {
        return name;
    }

    // Returns guardian type
    public String getType() {
        return type;
    }

    // Returns guardian relationship
    public String getRelation() {
        return relation;
    }

    // Returns guardian phone number
    public String getPhone() {
        return phone;
    }
}