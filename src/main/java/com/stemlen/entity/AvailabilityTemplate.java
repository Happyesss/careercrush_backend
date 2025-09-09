package com.stemlen.entity;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.AvailabilityTemplateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "availability_templates")
public class AvailabilityTemplate {
    
    @Id
    private Long id;
    
    private Long mentorId;
    private String templateName;
    private String description;
    
    // Weekly pattern
    private List<DailyAvailability> dailyAvailabilities;
    
    // Default session configuration
    private Integer defaultDurationMinutes;
    private String defaultSessionType;
    private Integer bufferTimeMinutes;
    private Integer preparationTimeMinutes;
    private Boolean allowRescheduling;
    private Integer maxReschedulingHours;
    private Boolean requireConfirmation;
    
    private Boolean isDefault;     // Is this the mentor's default template
    private Boolean isActive;
    
    /**
     * Convert entity to DTO
     */
    public AvailabilityTemplateDTO toDTO() {
        AvailabilityTemplateDTO dto = new AvailabilityTemplateDTO();
        dto.setId(this.id);
        dto.setMentorId(this.mentorId);
        dto.setTemplateName(this.templateName);
        dto.setDescription(this.description);
        
        if (this.dailyAvailabilities != null) {
            List<AvailabilityTemplateDTO.DailyAvailabilityDTO> dailyDTOs = this.dailyAvailabilities.stream()
                .map(DailyAvailability::toDTO)
                .toList();
            dto.setDailyAvailabilities(dailyDTOs);
        }
        
        dto.setDefaultDurationMinutes(this.defaultDurationMinutes);
        dto.setDefaultSessionType(this.defaultSessionType);
        dto.setBufferTimeMinutes(this.bufferTimeMinutes);
        dto.setPreparationTimeMinutes(this.preparationTimeMinutes);
        dto.setAllowRescheduling(this.allowRescheduling);
        dto.setMaxReschedulingHours(this.maxReschedulingHours);
        dto.setRequireConfirmation(this.requireConfirmation);
        dto.setIsDefault(this.isDefault);
        dto.setIsActive(this.isActive);
        
        return dto;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyAvailability {
        private Integer dayOfWeek;         // 1=Monday, 7=Sunday
        private Boolean isAvailable;
        private List<TimeSlotTemplate> timeSlots;
        
        public AvailabilityTemplateDTO.DailyAvailabilityDTO toDTO() {
            AvailabilityTemplateDTO.DailyAvailabilityDTO dto = new AvailabilityTemplateDTO.DailyAvailabilityDTO();
            dto.setDayOfWeek(this.dayOfWeek);
            dto.setIsAvailable(this.isAvailable);
            
            if (this.timeSlots != null) {
                List<AvailabilityTemplateDTO.DailyAvailabilityDTO.TimeSlotTemplateDTO> slotDTOs = 
                    this.timeSlots.stream()
                        .map(TimeSlotTemplate::toDTO)
                        .toList();
                dto.setTimeSlots(slotDTOs);
            }
            
            return dto;
        }
        
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class TimeSlotTemplate {
            private LocalTime startTime;
            private LocalTime endTime;
            private Integer sessionDurationMinutes;
            private String sessionTitle;
            private String sessionDescription;
            
            public AvailabilityTemplateDTO.DailyAvailabilityDTO.TimeSlotTemplateDTO toDTO() {
                AvailabilityTemplateDTO.DailyAvailabilityDTO.TimeSlotTemplateDTO dto = 
                    new AvailabilityTemplateDTO.DailyAvailabilityDTO.TimeSlotTemplateDTO();
                dto.setStartTime(this.startTime);
                dto.setEndTime(this.endTime);
                dto.setSessionDurationMinutes(this.sessionDurationMinutes);
                dto.setSessionTitle(this.sessionTitle);
                dto.setSessionDescription(this.sessionDescription);
                return dto;
            }
        }
    }
}
