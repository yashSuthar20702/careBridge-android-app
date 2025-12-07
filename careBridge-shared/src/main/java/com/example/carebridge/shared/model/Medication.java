package com.example.carebridge.shared.model;

import com.google.gson.annotations.SerializedName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Medication {

    @SerializedName("prescription_medicine_id")
    private int prescriptionMedicineId;

    @SerializedName("medicine_id")
    private int medicineId;

    @SerializedName("medicine_name")
    private String medicineName;

    @SerializedName("dosage")
    private String dosage;

    @SerializedName("morning")
    private boolean morning;

    @SerializedName("afternoon")
    private boolean afternoon;

    @SerializedName("evening")
    private boolean evening;

    @SerializedName("night")
    private boolean night;

    @SerializedName("with_food")
    private boolean withFood;

    private int durationDays;

    @SerializedName("extra_instructions")
    private String extraInstructions;

    private boolean isTaken = false;

    private Date scheduledDate;

    @SerializedName("start_date")
    private String startDateStr;

    @SerializedName("end_date")
    private String endDateStr;

    public int getPrescriptionMedicineId() { return prescriptionMedicineId; }
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public String getDosage() { return dosage; }
    public boolean isMorning() { return morning; }
    public boolean isAfternoon() { return afternoon; }
    public boolean isEvening() { return evening; }
    public boolean isNight() { return night; }
    public boolean isWithFood() { return withFood; }
    public String getExtraInstructions() { return extraInstructions; }
    public boolean isTaken() { return isTaken; }
    public void setTaken(boolean taken) { isTaken = taken; }
    public int getDurationDays() { return durationDays; }
    public Date getScheduledDate() { return scheduledDate; }

    // Call this after deserialization
    public void calculateDuration() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            this.scheduledDate = start;
            if (start != null && end != null) {
                long diff = end.getTime() - start.getTime();
                this.durationDays = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            this.durationDays = 0;
        }
    }

    // Summary helpers
    public String getTimeSummary() {
        StringBuilder time = new StringBuilder();
        if (morning) time.append("Morning, ");
        if (afternoon) time.append("Afternoon, ");
        if (evening) time.append("Evening, ");
        if (night) time.append("Night, ");
        if (time.length() > 0) time.setLength(time.length() - 2);
        return time.toString();
    }

    public String getFoodInstructionText() {
        return withFood ? "Take with food" : "Take before food";
    }

    public String getSummaryText() {
        return dosage + " • " + getTimeSummary() + " • " + getFoodInstructionText();
    }
}
