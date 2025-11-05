package com.example.carebridge.model;

public class Medication {

    private int medicine_id;
    private String medicine_name;
    private String dosage;
    private int morning;
    private int afternoon;
    private int evening;
    private int night;
    private int with_food;
    private int duration_days;
    private String extra_instructions;

    // 🔹 New field to track medicine taken status
    private boolean isTaken = false;

    // -------------------- Getters & Setters -------------------- //

    public int getMedicine_id() { return medicine_id; }
    public void setMedicine_id(int medicine_id) { this.medicine_id = medicine_id; }

    public String getMedicine_name() { return medicine_name; }
    public void setMedicine_name(String medicine_name) { this.medicine_name = medicine_name; }

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

    public int getWith_food() { return with_food; }
    public void setWith_food(int with_food) { this.with_food = with_food; }

    public int getDuration_days() { return duration_days; }
    public void setDuration_days(int duration_days) { this.duration_days = duration_days; }

    public String getExtra_instructions() { return extra_instructions; }
    public void setExtra_instructions(String extra_instructions) { this.extra_instructions = extra_instructions; }

    // -------------------- Helper Methods -------------------- //

    /**
     * Returns a readable summary of medication times (e.g., "Morning, Evening").
     */
    public String getTimeSummary() {
        StringBuilder time = new StringBuilder();
        if (morning == 1) time.append("Morning, ");
        if (afternoon == 1) time.append("Afternoon, ");
        if (evening == 1) time.append("Evening, ");
        if (night == 1) time.append("Night, ");

        // Remove trailing comma and space
        if (time.length() > 0)
            time.setLength(time.length() - 2);

        return time.toString();
    }

    /**
     * Returns whether the medicine should be taken with or without food.
     */
    public String getFoodInstructionText() {
        return with_food == 1 ? "Take with food" : "Take before food";
    }

    /**
     * Returns true if the medicine has been marked as taken.
     */
    public boolean isTaken() {
        return isTaken;
    }

    /**
     * Marks medicine as taken or not.
     */
    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    /**
     * Returns a short summary string for UI cards.
     */
    public String getSummaryText() {
        return dosage + " • " + getTimeSummary() + " • " + getFoodInstructionText();
    }
}
