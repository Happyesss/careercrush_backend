package com.stemlen.utility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.stemlen.dto.MentorshipPackageDTO;
import com.stemlen.dto.TrialSessionDTO;
import com.stemlen.entity.PackageModule;
import com.stemlen.repository.MentorRepository;
import com.stemlen.service.MentorshipPackageService;
import com.stemlen.service.TrialSessionService;

/**
 * Sample data creator for testing the mentorship packages system
 * This component creates sample packages and trial sessions when the application starts
 * Comment out @Component annotation in production
 * IMPORTANT: Only creates data if valid mentors exist
 */
// @Component
public class SampleDataCreator implements CommandLineRunner {
    
    @Autowired
    private MentorshipPackageService packageService;
    
    @Autowired
    private TrialSessionService trialSessionService;
    
    @Autowired
    private MentorRepository mentorRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only create sample data if mentors exist
        if (mentorRepository.count() > 0) {
            // Get the first available mentor ID
            Long firstMentorId = mentorRepository.findAll().get(0).getId();
            createSamplePackages(firstMentorId);
            createSampleTrialSessions(firstMentorId);
        } else {
            System.out.println("⚠️  No mentors found - skipping sample data creation. Please create mentors first.");
        }
    }
    
    private void createSamplePackages(Long mentorId) throws Exception {
        // Validate mentor exists before creating any packages
        if (!mentorRepository.existsById(mentorId)) {
            System.out.println("❌ Mentor ID " + mentorId + " does not exist - cannot create sample packages");
            return;
        }
        
        // Create 6-month package similar to Shubham Khanna's profile
        MentorshipPackageDTO sixMonthPackage = new MentorshipPackageDTO();
        sixMonthPackage.setMentorId(mentorId); // Use valid mentor ID
        sixMonthPackage.setPackageName("6 Months Complete Software Engineering Mentorship");
        sixMonthPackage.setDescription("Comprehensive mentorship covering DSA, System Design, Mock Interviews, and career guidance for software engineers");
        sixMonthPackage.setDurationMonths(6);
        sixMonthPackage.setTotalSessions(48);
        sixMonthPackage.setSessionsPerMonth(8);
        sixMonthPackage.setPricePerMonth(10000.0);
        sixMonthPackage.setTotalPrice(60000.0);
        sixMonthPackage.setTopicsCovered(Arrays.asList("DSA", "System Design", "LLD", "HLD", "Mock Interviews", "Career Guidance"));
        sixMonthPackage.setIsActive(true);
        sixMonthPackage.setIsFreeTrialIncluded(true);
        sixMonthPackage.setSessionType("Video Call");
        sixMonthPackage.setSessionDurationMinutes(60);
        
        // Set package inclusions (as per Preplaced documentation)
        sixMonthPackage.setHasUnlimitedChat(true);
        sixMonthPackage.setHasCuratedTasks(true);
        sixMonthPackage.setHasRegularFollowups(true);
        sixMonthPackage.setHasJobReferrals(true);
        sixMonthPackage.setHasCertification(true);
        sixMonthPackage.setHasRescheduling(true);
        
        // Create modules for 6 months
        List<PackageModule> modules = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            PackageModule module = new PackageModule();
            module.setMonthNumber(i);
            module.setModuleTitle("Month " + i + " of Mentorship");
            module.setModuleDescription("This Module Contains Following:");
            module.setSessionsInMonth(8);
            module.setTopicsInMonth(Arrays.asList("Advanced Topics for Month " + i));
            module.setLearningObjectives(Arrays.asList("Master concepts in month " + i));
            module.setDeliverables(Arrays.asList("Practice problems", "Mock interviews"));
            modules.add(module);
        }
        sixMonthPackage.setModules(modules);
        
        packageService.createPackage(sixMonthPackage);
        
        // Create 3-month package
        MentorshipPackageDTO threeMonthPackage = new MentorshipPackageDTO();
        threeMonthPackage.setMentorId(mentorId); // Use valid mentor ID
        threeMonthPackage.setPackageName("3 Months Intensive DSA Bootcamp");
        threeMonthPackage.setDescription("Focused DSA preparation with daily practice and weekly mock interviews");
        threeMonthPackage.setDurationMonths(3);
        threeMonthPackage.setTotalSessions(24);
        threeMonthPackage.setSessionsPerMonth(8);
        threeMonthPackage.setPricePerMonth(8000.0);
        threeMonthPackage.setTotalPrice(24000.0);
        threeMonthPackage.setTopicsCovered(Arrays.asList("Arrays", "Strings", "Trees", "Graphs", "Dynamic Programming"));
        threeMonthPackage.setIsActive(true);
        threeMonthPackage.setIsFreeTrialIncluded(true);
        threeMonthPackage.setSessionType("Video Call");
        threeMonthPackage.setSessionDurationMinutes(60);
        
        // Set package inclusions (as per Preplaced documentation)
        threeMonthPackage.setHasUnlimitedChat(true);
        threeMonthPackage.setHasCuratedTasks(true);
        threeMonthPackage.setHasRegularFollowups(true);
        threeMonthPackage.setHasJobReferrals(true);
        threeMonthPackage.setHasCertification(true);
        threeMonthPackage.setHasRescheduling(true);
        
        // Create modules for 3 months
        List<PackageModule> threeMonthModules = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            PackageModule module = new PackageModule();
            module.setMonthNumber(i);
            module.setModuleTitle("Month " + i + " of Mentorship");
            module.setModuleDescription("This Module Contains Following:");
            module.setSessionsInMonth(8);
            module.setTopicsInMonth(Arrays.asList("DSA Topics for Month " + i));
            module.setLearningObjectives(Arrays.asList("Master DSA patterns in month " + i));
            module.setDeliverables(Arrays.asList("Problem solving", "Code review"));
            threeMonthModules.add(module);
        }
        threeMonthPackage.setModules(threeMonthModules);
        
        packageService.createPackage(threeMonthPackage);
        
        // Create 1-month package
        MentorshipPackageDTO oneMonthPackage = new MentorshipPackageDTO();
        oneMonthPackage.setMentorId(mentorId); // Use valid mentor ID
        oneMonthPackage.setPackageName("1 Month Interview Preparation");
        oneMonthPackage.setDescription("Intensive interview preparation with daily mock interviews and feedback");
        oneMonthPackage.setDurationMonths(1);
        oneMonthPackage.setTotalSessions(8);
        oneMonthPackage.setSessionsPerMonth(8);
        oneMonthPackage.setPricePerMonth(5000.0);
        oneMonthPackage.setTotalPrice(5000.0);
        oneMonthPackage.setTopicsCovered(Arrays.asList("Mock Interviews", "Resume Review", "System Design Basics"));
        oneMonthPackage.setIsActive(true);
        oneMonthPackage.setIsFreeTrialIncluded(true);
        oneMonthPackage.setSessionType("Video Call");
        oneMonthPackage.setSessionDurationMinutes(60);
        
        // Set package inclusions (as per Preplaced documentation)
        oneMonthPackage.setHasUnlimitedChat(true);
        oneMonthPackage.setHasCuratedTasks(true);
        oneMonthPackage.setHasRegularFollowups(true);
        oneMonthPackage.setHasJobReferrals(true);
        oneMonthPackage.setHasCertification(true);
        oneMonthPackage.setHasRescheduling(true);
        
        // Create module for 1 month
        List<PackageModule> oneMonthModules = new ArrayList<>();
        PackageModule module = new PackageModule();
        module.setMonthNumber(1);
        module.setModuleTitle("Month 1 of Mentorship");
        module.setModuleDescription("This Module Contains Following:");
        module.setSessionsInMonth(8);
        module.setTopicsInMonth(Arrays.asList("Interview Skills", "Communication"));
        module.setLearningObjectives(Arrays.asList("Master interview techniques"));
        module.setDeliverables(Arrays.asList("Mock interviews", "Feedback reports"));
        oneMonthModules.add(module);
        oneMonthPackage.setModules(oneMonthModules);
        
        packageService.createPackage(oneMonthPackage);
        
        System.out.println("✅ Sample packages created successfully for mentor ID: " + mentorId);
    }
    
    private void createSampleTrialSessions(Long mentorId) throws Exception {
        // Validate mentor exists before creating any trial sessions
        if (!mentorRepository.existsById(mentorId)) {
            System.out.println("❌ Mentor ID " + mentorId + " does not exist - cannot create sample trial sessions");
            return;
        }
        
        // Create trial sessions for September 2025
        List<LocalDateTime> availableSlots = Arrays.asList(
            LocalDateTime.of(2025, 9, 3, 21, 30), // Sep 3, 9:30 PM
            LocalDateTime.of(2025, 9, 4, 21, 30), // Sep 4, 9:30 PM
            LocalDateTime.of(2025, 9, 5, 21, 30), // Sep 5, 9:30 PM
            LocalDateTime.of(2025, 9, 6, 21, 30), // Sep 6, 9:30 PM
            LocalDateTime.of(2025, 9, 7, 21, 30)  // Sep 7, 9:30 PM
        );
        
        trialSessionService.createMultipleAvailableSlots(mentorId, availableSlots, 30);
        
        System.out.println("✅ Sample trial sessions created successfully for mentor ID: " + mentorId);
    }
}