package com.example.carebridge.shared.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MedicineLog {
    @SerializedName("log_id")
    private int logId;

    @SerializedName("case_id")
    private String caseId;

    @SerializedName("prescription_id")
    private int prescriptionId;

    @SerializedName("medicine_id")
    private int medicineId;

    @SerializedName("scheduled_time")
    private String scheduledTime;

    @SerializedName("actual_time")
    private String actualTime;

    @SerializedName("taken_status")
    private String takenStatus;

    @SerializedName("dose_taken")
    private int doseTaken;

    @SerializedName("notes")
    private String notes;

    public int getLogId() { return logId; }
    public String getCaseId() { return caseId; }
    public int getPrescriptionId() { return prescriptionId; }
    public int getMedicineId() { return medicineId; }
    public String getScheduledTime() { return scheduledTime; }
    public String getActualTime() { return actualTime; }
    public String getTakenStatus() { return takenStatus; }
    public int getDoseTaken() { return doseTaken; }
    public String getNotes() { return notes; }

    public boolean isTaken() {
        return "Taken".equalsIgnoreCase(takenStatus);
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(takenStatus);
    }

    public boolean isNotTaken() {
        return "Not Taken".equalsIgnoreCase(takenStatus);
    }

    /** Parse scheduledTime string to Date object */
    public Date getScheduledDate() {
        if (scheduledTime == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return sdf.parse(scheduledTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
