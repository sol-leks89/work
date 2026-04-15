package net.brakingpoint.trackInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrakingZone {
    
    @JsonProperty("id")
    private String id;

    @JsonProperty("locationMeters")
    private double locationMeters;

    @JsonProperty("apexSpeed")
    private double apexSpeed;

    // Optional: If your JSON contains the G-loads the Porsche achieved
    @JsonProperty("baselineLongG")
    private double baselineLongG;

    @JsonProperty("baselineLatG")
    private double baselineLatG;

    // Default constructor for Jackson
    public BrakingZone() {}

    // Getters
    public String getId() { return id; }
    public double getLocationMeters() { return locationMeters; }
    public double getApexSpeed() { return apexSpeed; }
    public double getBaselineLongG() { return baselineLongG; }
    public double getBaselineLatG() { return baselineLatG; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setLocationMeters(double locationMeters) { this.locationMeters = locationMeters; }
    public void setApexSpeed(double apexSpeed) { this.apexSpeed = apexSpeed; }
    public void setBaselineLongG(double baselineLongG) { this.baselineLongG = baselineLongG; }
    public void setBaselineLatG(double baselineLatG) { this.baselineLatG = baselineLatG; }
}
