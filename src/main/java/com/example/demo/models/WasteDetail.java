package com.example.demo.models;

public class WasteDetail {
    private String foodName;
    private double wasteAmount;
    private String genreName; 
    private double wasteRate; 

    public WasteDetail(String foodName, double wasteAmount, String genreName, double wasteRate) {
        this.foodName = foodName;
        this.wasteAmount = wasteAmount;
        this.genreName = genreName;
        this.wasteRate = wasteRate;
    }
    
    
    
    public String getFoodName() { return foodName; }
    public double getWasteAmount() { return wasteAmount; }
    public String getGenreName() { return genreName; }
    public double getWasteRate() { return wasteRate; }
}