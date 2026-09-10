package com.emergency.service;

import com.emergency.exception.InvalidRequestException;
import com.emergency.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DispatchServiceTest {
    private DispatchService dispatchService;

    @BeforeEach
    void setUp() {
        dispatchService = new DispatchService();
    }

    @Test
    void testSuccessfulAllocationBasedOnDistance() {
        Ambulance farAmbulance = new Ambulance("AMB-FAR", AmbulanceType.ICU_AMBULANCE, "John Doe", 10.0, 10.0);
        Ambulance closeAmbulance = new Ambulance("AMB-CLOSE", AmbulanceType.ICU_AMBULANCE, "Jane Smith", 1.0, 1.0);
        
        dispatchService.registerAmbulance(farAmbulance);
        dispatchService.registerAmbulance(closeAmbulance);

        EmergencyRequest request = new EmergencyRequest("P101", "Cardiac Arrest", EmergencyPriority.CRITICAL, 0.0, 0.0, "City Hospital", AmbulanceType.ICU_AMBULANCE);
        dispatchService.submitEmergencyRequest(request);

        assertEquals("ALLOCATED", request.getStatus());
        assertEquals("AMB-CLOSE", request.getAssignedAmbulanceId());
        assertEquals(AmbulanceState.DISPATCHED, closeAmbulance.getState());
    }

    @Test
    void testWaitingQueueAndAutoAllocation() {
        EmergencyRequest request = new EmergencyRequest("P102", "Trauma", EmergencyPriority.CRITICAL, 0.0, 0.0, "City Hospital", AmbulanceType.BASIC);
        dispatchService.submitEmergencyRequest(request);

        assertEquals("QUEUED", request.getStatus());
        assertEquals(1, dispatchService.getWaitingQueue().size());

        Ambulance basicAmb = new Ambulance("AMB-BASIC", AmbulanceType.BASIC, "Driver One", 2.0, 2.0);
        dispatchService.registerAmbulance(basicAmb);

        assertEquals("ALLOCATED", request.getStatus());
        assertEquals("AMB-BASIC", request.getAssignedAmbulanceId());
        assertTrue(dispatchService.getWaitingQueue().isEmpty());
    }

    @Test
    void testInvalidRequestExceptionThrown() {
        EmergencyRequest badRequest = new EmergencyRequest("", "Fever", EmergencyPriority.NORMAL, 0.0, 0.0, "", AmbulanceType.BASIC);
        assertThrows(InvalidRequestException.class, () -> dispatchService.submitEmergencyRequest(badRequest));
    }
}

