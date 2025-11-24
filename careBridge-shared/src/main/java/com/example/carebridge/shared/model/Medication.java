package com.example.carebridge.shared.model;

import java.util.Date;

public class Medication {
    private int medicineId;
    private String medicineName;
    private String dosage;
    private int morning;
    private int afternoon;
    private int evening;
    private int night;
    private int withFood;
    private int durationDays;
    private String extraInstructions;
    private boolean isTaken = false;
    private Date scheduledDate;

    // Getters and Setters
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public int getMorning() { return morning; }
    public void setMorning(int morning) { this.morning = morning; }

    public int getAfternoon() { return afternoon; }
    public void setAfternoon(int afternoon) { this.afternoon = afternoon; }

    public int getEvening() { return evening; }
    public void setEvening(int evening) { this.evening = evening; }

    public int getNight() { return night; }
    public void setNight(int night) { this.night = night; }

    public int getWithFood() { return withFood; }
    public void setWithFood(int withFood) { this.withFood = withFood; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public String getExtraInstructions() { return extraInstructions; }
    public void setExtraInstructions(String extraInstructions) { this.extraInstructions = extraInstructions; }

    public boolean isTaken() { return isTaken; }
    public void setTaken(boolean taken) { isTaken = taken; }

    public Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }

    // Helper Methods
    public String getTimeSummary() {
        StringBuilder time = new StringBuilder();
        if (morning == 1) time.append("Morning, ");
        if (afternoon == 1) time.append("Afternoon, ");
        if (evening == 1) time.append("Evening, ");
        if (night == 1) time.append("Night, ");
        if (time.length() > 0) time.setLength(time.length() - 2);
        return time.toString();
    }

    public String getFoodInstructionText() {
        return withFood == 1 ? "Take with food" : "Take before food";
    }

    public String getSummaryText() {
        return dosage + " • " + getTimeSummary() + " • " + getFoodInstructionText();
    }
}