package com.stemlen.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityTemplateDTO {
    
    private Long id;
    private Long mentorId;
    private String templateName;
    private String description;
    
    // Weekly pattern
    private List<DailyAvailabilityDTO> dailyAvailabilities;
    
    // Default session configuration
    private Integer defaultDurationMinutes = 30;
    private String defaultSessionType = "Video Call";
    private Integer bufferTimeMinutes = 5;
    private Integer preparationTimeMinutes = 10;
    private Boolean allowRescheduling = true;
    private Integer maxReschedulingHours = 24;
    private Boolean requireConfirmation = false;
    
    private Boolean isDefault = false;     // Is this the mentor's default template
    private Boolean isActive = true;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyAvailabilityDTO {
        private Integer dayOfWeek;         // 1=Monday, 7=Sunday
        private Boolean isAvailable = false;
        private List<TimeSlotTemplateDTO> timeSlots;
        
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class TimeSlotTemplateDTO {
            private LocalTime startTime;
            private LocalTime endTime;
            private Integer sessionDurationMinutes = 30;
            private String sessionTitle;
            private String sessionDescription;
        }
    }
}
