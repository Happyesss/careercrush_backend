package com.stemlen.dto;

public enum TrialSessionStatus {
    AVAILABLE,    // Time slot is available for booking
    BOOKED,       // Trial session has been booked
    COMPLETED,    // Trial session has been completed
    CANCELLED,    // Trial session was cancelled
    NO_SHOW       // Mentee didn't show up for trial
}