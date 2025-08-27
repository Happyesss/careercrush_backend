package com.stemlen.dto;

public enum TargetAudience {
    FRESHERS("FRESHERS"),
    EXPERIENCED("EXPERIENCED"),
    CAREER_CHANGE("CAREER_CHANGE"),
    INTERVIEW_PREP("INTERVIEW_PREP"),
    SKILL_DEVELOPMENT("SKILL_DEVELOPMENT"),
    LEADERSHIP("LEADERSHIP");
    
    private final String value;
    
    TargetAudience(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}