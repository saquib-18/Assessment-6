package com.emergency.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.emergency.exception.InvalidRequestException;
import com.emergency.model.Ambulance;
import com.emergency.model.AmbulanceState;
import com.emergency.model.EmergencyRequest;

public class DispatchService {
    private final Map<String, Ambulance> ambulances = new ConcurrentHashMap<>();
    private final Map<String, EmergencyRequest> requestsLog = new ConcurrentHashMap<>();
    
    // Thread-safe waiting queue sorting by priority rank first, then shortest processing step
    private final PriorityBlockingQueue<EmergencyRequest> waitingQueue = new PriorityBlockingQueue<>(11,
            Comparator.comparingInt((EmergencyRequest r) -> r.getPriority().getRank()));

    public void registerAmbulance(Ambulance ambulance) {
        ambulances.put(ambulance.getAmbulanceId(), ambulance);
        processWaitingQueue(); // Check if newly registered resource can fulfill waiting requests
    }

    public synchronized EmergencyRequest submitEmergencyRequest(EmergencyRequest request) {
        validateRequest(request);
        requestsLog.put(request.getRequestId(), request);

        Optional<Ambulance> bestAmbulance = findBestAvailableAmbulance(request);

        if (bestAmbulance.isPresent()) {
            allocateAmbulance(request, bestAmbulance.get());
        } else {
            request.setStatus("QUEUED");
            waitingQueue.add(request);
        }
        return request;
    }

    private void validateRequest(EmergencyRequest request) {
        if (request.getPatientId() == null || request.getPatientId().trim().isEmpty() ||
            request.getDestinationHospital() == null || request.getDestinationHospital().trim().isEmpty()) {
            throw new InvalidRequestException("Invalid emergency request data parameters.");
        }
    }

    private Optional<Ambulance> findBestAvailableAmbulance(EmergencyRequest request) {
        return ambulances.values().stream()
                .filter(a -> a.getState() == AmbulanceState.AVAILABLE)
                .filter(a -> a.getType() == request.getRequiredType())
                .min(Comparator.comparingDouble(a -> a.calculateDistance(request.getPickupX(), request.getPickupY())));
    }

    private void allocateAmbulance(EmergencyRequest request, Ambulance ambulance) {
        ambulance.setState(AmbulanceState.DISPATCHED);
        request.setAssignedAmbulanceId(ambulance.getAmbulanceId());
        request.setStatus("ALLOCATED");
    }

    public synchronized void updateAmbulanceState(String ambulanceId) {
        Ambulance ambulance = ambulances.get(ambulanceId);
        if (ambulance == null) {
            throw new InvalidRequestException("Ambulance asset not found.");
        }

        AmbulanceState nextState = ambulance.getState().next();
        ambulance.setState(nextState);

        if (nextState == AmbulanceState.AVAILABLE) {
            processWaitingQueue();
        }
    }

    private synchronized void processWaitingQueue() {
        List<EmergencyRequest> reQueueList = new ArrayList<>();
        while (!waitingQueue.isEmpty()) {
            EmergencyRequest request = waitingQueue.poll();
            Optional<Ambulance> bestAmbulance = findBestAvailableAmbulance(request);

            if (bestAmbulance.isPresent()) {
                allocateAmbulance(request, bestAmbulance.get());
            } else {
                reQueueList.add(request);
            }
        }
        waitingQueue.addAll(reQueueList);
    }

    public double calculateETA(String requestId) {
        EmergencyRequest request = requestsLog.get(requestId);
        if (request == null || request.getAssignedAmbulanceId() == null) {
            return -1.0; 
        }
        Ambulance ambulance = ambulances.get(request.getAssignedAmbulanceId());
        double distance = ambulance.calculateDistance(request.getPickupX(), request.getPickupY());
        // Assume an average constant response speed of 0.8 units per minute
        return distance / 0.8;
    }

    public List<EmergencyRequest> getCompleteHistory() {
        return new ArrayList<>(requestsLog.values());
    }

    public Map<String, Ambulance> getAmbulances() { return ambulances; }
    public Queue<EmergencyRequest> getWaitingQueue() { return waitingQueue; }
}

