package com.emergency.model;

import java.util.UUID;

public class EmergencyRequest {
    private final String requestId;
    private final String patientId;
    private final String emergencyType;
    private final EmergencyPriority priority;
    private final double pickupX;
    private final double pickupY;
    private final String destinationHospital;
    private final AmbulanceType requiredType;
    private String assignedAmbulanceId;
    private String status;

    public EmergencyRequest(String patientId, String emergencyType, EmergencyPriority priority, 
                            double pickupX, double pickupY, String destinationHospital, AmbulanceType requiredType) {
        this.requestId = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.emergencyType = emergencyType;
        this.priority = priority;
        this.pickupX = pickupX;
        this.pickupY = pickupY;
        this.destinationHospital = destinationHospital;
        this.requiredType = requiredType;
        this.status = "PENDING";
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public String getPatientId() { return patientId; }
    public String getEmergencyType() { return emergencyType; }
    public EmergencyPriority getPriority() { return priority; }
    public double getPickupX() { return pickupX; }
    public double getPickupY() { return pickupY; }
    public String getDestinationHospital() { return destinationHospital; }
    public AmbulanceType getRequiredType() { return requiredType; }
    public String getAssignedAmbulanceId() { return assignedAmbulanceId; }
    public void setAssignedAmbulanceId(String assignedAmbulanceId) { this.assignedAmbulanceId = assignedAmbulanceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

