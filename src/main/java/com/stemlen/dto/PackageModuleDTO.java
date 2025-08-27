package com.stemlen.dto;

import java.util.List;

import com.stemlen.entity.PackageModule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageModuleDTO {
    private Integer monthNumber;
    private String moduleTitle;
    private String moduleDescription;
    private Integer sessionsInMonth;
    private List<String> topicsInMonth;
    private List<String> learningObjectives;
    private List<String> deliverables;
    
    public PackageModule toEntity() {
        PackageModule entity = new PackageModule();
        entity.setMonthNumber(this.monthNumber);
        entity.setModuleTitle(this.moduleTitle);
        entity.setModuleDescription(this.moduleDescription);
        entity.setSessionsInMonth(this.sessionsInMonth);
        entity.setTopicsInMonth(this.topicsInMonth);
        entity.setLearningObjectives(this.learningObjectives);
        entity.setDeliverables(this.deliverables);
        return entity;
    }
}