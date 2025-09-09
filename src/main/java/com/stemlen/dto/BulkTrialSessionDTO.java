package com.stemlen.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkTrialSessionDTO {
    
    // Mentor will be set automatically from JWT token
    private Long packageId;                // Optional associated package
    
    // Date range for creating sessions
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    // Time slots for each day
    @NotNull(message = "Time slots are required")
    private List<TimeSlotDTO> timeSlots;
    
    // Days of week to include (1=Monday, 7=Sunday)
    private List<Integer> daysOfWeek;      // e.g., [1, 2, 3, 4, 5] for Mon-Fri
    
    // Session configuration
    private String sessionType = "Video Call";
    private String timeZone = "UTC";
    private Integer bufferTimeMinutes = 5;
    private Integer preparationTimeMinutes = 10;
    private Boolean allowRescheduling = true;
    private Integer maxReschedulingHours = 24;
    private Boolean requireConfirmation = false;
    private String specialInstructions;
    
    // Template for reusing this pattern
    private String availabilityTemplate;
    
    // Recurring configuration
    private Boolean createRecurring = false;
    private String recurringPattern = "WEEKLY"; // DAILY, WEEKLY, MONTHLY
    private Integer recurringWeeks = 4;        // How many weeks to repeat
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeSlotDTO {
        @NotNull(message = "Start time is required")
        private LocalTime startTime;
        
        @NotNull(message = "Duration is required")
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        private Integer durationMinutes;
        
        private String sessionTitle;
        private String sessionDescription;
    }
}
