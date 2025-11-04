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

    // Helper for readable timing text
    public String getTimeSummary() {
        StringBuilder time = new StringBuilder();
        if (morning == 1) time.append("Morning ");
        if (afternoon == 1) time.append("Afternoon ");
        if (evening == 1) time.append("Evening ");
        if (night == 1) time.append("Night ");
        return time.toString().trim();
    }
}
