package com.example.carebridge.shared.model;


import java.io.Serializable;

public class GuardianInfo implements Serializable {
    private String guardian_id;
    private String full_name;
    private String phone;
    private String email;
    private String address;
    private String type;
    private String occupation;
    private String availability;
    private String notes;
    private String created_at;

    public String getGuardian_id() { return guardian_id; }
    public void setGuardian_id(String guardian_id) { this.guardian_id = guardian_id; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    @Override
    public String toString() {
        return "GuardianInfo{" +
                "guardian_id='" + guardian_id + '\'' +
                ", full_name='" + full_name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", occupation='" + occupation + '\'' +
                ", availability='" + availability + '\'' +
                ", notes='" + notes + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
