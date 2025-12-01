package com.example.carebridge.shared.model;

import java.util.Date;

public class Medication {
    private int medicineId;
    private String medicineName;
    private String dosage;

    private boolean morning;
    private boolean afternoon;
    private boolean evening;
    private boolean night;
    private boolean withFood;

    private int durationDays;
    private String extraInstructions;
    private boolean isTaken = false;
    private Date scheduledDate;

    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public boolean isMorning() { return morning; }
    public void setMorning(boolean morning) { this.morning = morning; }

    public boolean isAfternoon() { return afternoon; }
    public void setAfternoon(boolean afternoon) { this.afternoon = afternoon; }

    public boolean isEvening() { return evening; }
    public void setEvening(boolean evening) { this.evening = evening; }

    public boolean isNight() { return night; }
    public void setNight(boolean night) { this.night = night; }

    public boolean isWithFood() { return withFood; }
    public void setWithFood(boolean withFood) { this.withFood = withFood; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public String getExtraInstructions() { return extraInstructions; }
    public void setExtraInstructions(String extraInstructions) { this.extraInstructions = extraInstructions; }

    public boolean isTaken() { return isTaken; }
    public void setTaken(boolean taken) { isTaken = taken; }

    public Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }

    // Summary helpers
    public String getTimeSummary() {
        StringBuilder time = new StringBuilder();
        if (morning) time.append("Morning, ");
        if (afternoon) time.append("Afternoon, ");
        if (evening) time.append("Evening, ");
        if (night) time.append("Night, ");

        if (time.length() > 0)
            time.setLength(time.length() - 2);

        return time.toString();
    }

    public String getFoodInstructionText() {
        return withFood ? "Take with food" : "Take before food";
    }

    public String getSummaryText() {
        return dosage + " • " + getTimeSummary() + " • " + getFoodInstructionText();
    }
}
