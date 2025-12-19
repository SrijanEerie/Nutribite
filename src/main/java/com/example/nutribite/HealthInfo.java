package com.example.nutribite;

import com.google.gson.annotations.SerializedName;

public class HealthInfo {
    private int age;
    private String weight;
    private String height;

    @SerializedName("dietary_goal")
    private String dietaryGoal;

    @SerializedName("activity_level")
    private String activityLevel;

    @SerializedName("water_intake")
    private String waterIntake;

    @SerializedName("sleep_hours")
    private String sleepHours;

    // Getters and setters for all fields
    public int getAge() { return age; }
    public String getWeight() { return weight; }
    public String getHeight() { return height; }
    public String getDietaryGoal() { return dietaryGoal; }
    public String getActivityLevel() { return activityLevel; }
    public String getWaterIntake() { return waterIntake; }
    public String getSleepHours() { return sleepHours; }
}