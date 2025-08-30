package com.stemlen.dto;

public enum TrialSessionStatus {
    AVAILABLE,      // Session slot is available for booking
    BOOKED,         // Session has been booked by a mentee
    COMPLETED,      // Session has been completed
    CANCELLED,      // Session has been cancelled
    NO_SHOW,        // Mentee didn't show up
    RESCHEDULED     // Session was rescheduled
}
