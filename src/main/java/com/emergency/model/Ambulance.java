package com.emergency.model;

public class Ambulance {
    private String ambulanceId;
    private AmbulanceType type;
    private AmbulanceState state;
    private String driverDetails;
    private double currentX;
    private double currentY;

    public Ambulance(String ambulanceId, AmbulanceType type, String driverDetails, double currentX, double currentY) {
        this.ambulanceId = ambulanceId;
        this.type = type;
        this.state = AmbulanceState.AVAILABLE;
        this.driverDetails = driverDetails;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    // Manhattan distance calculation helper
    public double calculateDistance(double targetX, double targetY) {
        return Math.abs(this.currentX - targetX) + Math.abs(this.currentY - targetY);
    }

    // Getters and Setters
    public String getAmbulanceId() { return ambulanceId; }
    public AmbulanceType getType() { return type; }
    public AmbulanceState getState() { return state; }
    public void setState(AmbulanceState state) { this.state = state; }
    public String getDriverDetails() { return driverDetails; }
    public double getCurrentX() { return currentX; }
    public double getCurrentY() { return currentY; }
    public void setLocation(double x, double y) { this.currentX = x; this.currentY = y; }
}

