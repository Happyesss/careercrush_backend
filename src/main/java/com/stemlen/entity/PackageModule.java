package com.stemlen.entity;

import java.util.List;

import com.stemlen.dto.PackageModuleDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageModule {
    private Integer monthNumber; // Month 1, 2, 3, etc.
    private String moduleTitle; // e.g., "Month 1 of Mentorship"
    private String moduleDescription; // Description of what this month covers
    private Integer sessionsInMonth; // Number of sessions in this month
    private List<String> topicsInMonth; // Topics covered in this month
    private List<String> learningObjectives; // What mentee will learn
    private List<String> deliverables; // What will be delivered
    
    public PackageModuleDTO toDTO() {
        return new PackageModuleDTO(
            this.monthNumber, this.moduleTitle, this.moduleDescription,
            this.sessionsInMonth, this.topicsInMonth, this.learningObjectives,
            this.deliverables
        );
    }
}