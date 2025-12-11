package com.example.carebridge.wear.models;

public class Guardian {
    private String name;
    private String type;
    private String relation;
    private String phone;

    public Guardian(String name, String type, String relation, String phone) {
        this.name = name;
        this.type = type;
        this.relation = relation;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}