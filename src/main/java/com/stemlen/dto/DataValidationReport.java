package com.stemlen.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataValidationReport {
    private LocalDateTime reportTimestamp;
    private String reportType;
    private Boolean hasIssues;
    private Integer totalIssuesFound;
    private List<String> issueDescriptions;
    private List<ValidationIssue> detailedIssues;
    private String summary;
    private String recommendedActions;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidationIssue {
        private String issueType;
        private String entityType;
        private Long entityId;
        private String description;
        private String severity; // HIGH, MEDIUM, LOW
        private String suggestedFix;
    }
}