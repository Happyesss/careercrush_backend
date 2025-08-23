package com.stemlen.utility;

import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import com.stemlen.entity.Sequence;
import com.stemlen.exception.PortalException;

@Component
public class Utilities {

    private static MongoOperations mongoOperation;

    @Autowired
    public void setMongoOperation(MongoOperations mongoOperation) {
        Utilities.mongoOperation = mongoOperation;
    }

    /**
     * Gets the next sequence number for a given key using MongoDB.
     *
     * @param key the sequence key (e.g., "users")
     * @return the next sequence number
     * @throws PortalException if the sequence cannot be generated
     */
    public static Long getNextSequence(String key) throws PortalException {
        Query query = new Query(Criteria.where("_id").is(key));

        // Increment sequence with upsert to create if doesn't exist
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true)
                .upsert(true); // Allow creating new sequence if it doesn't exist

        Sequence seq = mongoOperation.findAndModify(query, update, options, Sequence.class);

        if (seq == null) {
            throw new PortalException("Unable to generate sequence for key: " + key);
        }

        return seq.getSeq();
    }

    /**
     * Generates a 6-digit OTP.
     *
     * @return the generated OTP
     */
    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Manually initializes a sequence for a given key.
     *
     * @param key the sequence key
     * @param initialValue the initial value (default: 0)
     * @throws PortalException if the sequence cannot be initialized
     */
    public static void initializeSequence(String key, Long initialValue) throws PortalException {
        try {
            Query query = new Query(Criteria.where("_id").is(key));
            Sequence existingSequence = mongoOperation.findOne(query, Sequence.class);
            
            if (existingSequence == null) {
                Sequence newSequence = new Sequence(key, initialValue != null ? initialValue : 0L);
                mongoOperation.save(newSequence);
            }
        } catch (Exception e) {
            throw new PortalException("Failed to initialize sequence for key: " + key + ". Error: " + e.getMessage());
        }
    }
}
