package com.example.carebridge.shared.model;

import java.util.List;

public class PrescriptionResponse {
    private boolean status;
    private List<Prescription> prescriptions;

    public boolean isStatus() {
        return status;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }
}
