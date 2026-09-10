package com.emergency.model;

public enum AmbulanceState {
    AVAILABLE,
    DISPATCHED,
    EN_ROUTE,
    PATIENT_PICKED_UP,
    HOSPITAL_ARRIVED;

    public AmbulanceState next() {
        switch (this) {
            case AVAILABLE: return DISPATCHED;
            case DISPATCHED: return EN_ROUTE;
            case EN_ROUTE: return PATIENT_PICKED_UP;
            case PATIENT_PICKED_UP: return HOSPITAL_ARRIVED;
            case HOSPITAL_ARRIVED: return AVAILABLE;
            default: return AVAILABLE;
        }
    }
}

