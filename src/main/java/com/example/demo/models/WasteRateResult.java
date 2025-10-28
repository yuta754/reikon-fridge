package com.example.demo.models;

public class WasteRateResult {
    private String genreName;
    private double wasteRate;

    public WasteRateResult(String genreName, double wasteRate) {
        this.genreName = genreName;
        this.wasteRate = wasteRate;
    }

    // Getters
    public String getGenreName() {
        return genreName;
    }
    public double getWasteRate() {
        return wasteRate;
    }
    
}