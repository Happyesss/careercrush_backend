package com.stemlen.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.stemlen.entity.Sequence;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initializeSequences();
    }

    private void initializeSequences() {
        // Initialize sequences for different collections
        String[] sequenceKeys = {"users", "profiles", "hackathon", "jobs", "applications", 
                               "mentors", "mentorshipPackages", "trialSessions"};
        
        for (String key : sequenceKeys) {
            initializeSequence(key);
        }
        
        System.out.println("✅ Database sequences initialized successfully!");
    }

    private void initializeSequence(String key) {
        Query query = new Query(Criteria.where("_id").is(key));
        Sequence existingSequence = mongoOperations.findOne(query, Sequence.class);
        
        if (existingSequence == null) {
            Sequence newSequence = new Sequence(key, 0L);
            mongoOperations.save(newSequence);
            System.out.println("✅ Created sequence for key: " + key);
        } else {
            System.out.println("ℹ️  Sequence already exists for key: " + key);
        }
    }
}
