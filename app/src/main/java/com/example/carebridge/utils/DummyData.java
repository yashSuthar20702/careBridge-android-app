package com.example.carebridge.utils;

import com.example.carebridge.model.User;
import java.util.ArrayList;
import java.util.List;

public class DummyData {

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();

        // Patient user
        users.add(new User(1, "patient", "password123", "patient@carebridge.com", "John Patient", "Patient"));

        // Guardian user
        users.add(new User(2, "guardian", "password123", "guardian@carebridge.com", "Jane Guardian", "Guardian"));

        return users;
    }

    public static User getPatientUser() {
        return new User(1, "patient", "password123", "patient@carebridge.com", "John Patient", "Patient");
    }

    public static User getGuardianUser() {
        return new User(2, "guardian", "password123", "guardian@carebridge.com", "Jane Guardian", "Guardian");
    }
}