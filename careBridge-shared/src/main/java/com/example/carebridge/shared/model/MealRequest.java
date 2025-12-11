package com.example.carebridge.shared.model;

public class MealRequest {

    private String case_id;
    private String guardian_id;
    private String meal_date;
    private String morning_meal;
    private String afternoon_meal;
    private String evening_meal;
    private String night_meal;
    private String extra_information;

    public MealRequest(String case_id, String guardian_id, String meal_date,
                       String morning_meal, String afternoon_meal,
                       String evening_meal, String night_meal,
                       String extra_information) {

        this.case_id = case_id;
        this.guardian_id = guardian_id;
        this.meal_date = meal_date;
        this.morning_meal = morning_meal;
        this.afternoon_meal = afternoon_meal;
        this.evening_meal = evening_meal;
        this.night_meal = night_meal;
        this.extra_information = extra_information;
    }

    public String getCaseId() { return case_id; }
    public String getGuardianId() { return guardian_id; }
    public String getMealDate() { return meal_date; }
    public String getMorningMeal() { return morning_meal; }
    public String getAfternoonMeal() { return afternoon_meal; }
    public String getEveningMeal() { return evening_meal; }
    public String getNightMeal() { return night_meal; }
    public String getExtraInformation() { return extra_information; }
}
