package com.example.nutribite;

public class FoodItem {
    private String name;
    private String description;
    private String tag;
    private String allergen;
    private boolean isAllergen;

    public FoodItem(String name, String description, String tag, String allergen, boolean isAllergen) {
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.allergen = allergen;
        this.isAllergen = isAllergen;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTag() {
        return tag;
    }

    public String getAllergen() {
        return allergen;
    }

    public boolean isAllergen() {
        return isAllergen;
    }
}
