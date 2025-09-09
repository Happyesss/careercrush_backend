package com.stemlen.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTrialSlotRequest {
    
    // mentorId is NOT included here - will be set from JWT token
    
    private Long packageId;         // Optional associated mentorship package
    
    @NotNull(message = "Scheduled date/time is required")
    private LocalDateTime scheduledDateTime;
    
    private Integer durationMinutes = 30; // Default 30 minutes
    private String sessionType = "Video Call";
    
    // Enhanced scheduling fields
    private String timeZone = "UTC";        // Time zone for the session
    private Integer bufferTimeMinutes = 5;  // Buffer time between sessions
    private Integer preparationTimeMinutes = 10; // Preparation time before session
    private Boolean allowRescheduling = true; // Allow mentees to reschedule
    private Integer maxReschedulingHours = 24; // Max hours before session to reschedule
    private Boolean requireConfirmation = false; // Require manual confirmation
    private String specialInstructions;    // Special instructions for mentees
    
    // Session configuration
    private String sessionTitle;           // Custom title for the session
    private String sessionDescription;     // Brief description
}
