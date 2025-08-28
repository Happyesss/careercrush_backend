package com.stemlen.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.stemlen.dto.ResponseDTO;
import com.stemlen.dto.SubscriptionDTO;
import com.stemlen.dto.SubscriptionStatus;
import com.stemlen.exception.PortalException;
import com.stemlen.service.SubscriptionService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/subscriptions")
public class SubscriptionAPI {

    @Autowired
    private SubscriptionService service;

    // Quote endpoint to compute final price based on plan months and discounts
    @GetMapping("/quote")
    public ResponseEntity<SubscriptionDTO> quote(
            @RequestParam Long mentorId,
            @RequestParam Long packageId,
            @RequestParam Integer planMonths,
            @RequestParam(required = false, defaultValue = "0") Double checkoutPercent,
            @RequestParam(required = false, defaultValue = "0") Double studentPercent) throws PortalException {
        return new ResponseEntity<>(service.quote(mentorId, packageId, planMonths, checkoutPercent, studentPercent), HttpStatus.OK);
    }

    // Create subscription (after payment confirmation)
    @PostMapping("/create")
    public ResponseEntity<SubscriptionDTO> create(@RequestBody @Valid SubscriptionDTO dto) throws PortalException {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SubscriptionDTO> get(@PathVariable Long id) throws PortalException {
        return new ResponseEntity<>(service.get(id), HttpStatus.OK);
    }

    @GetMapping("/mentee/{menteeId}")
    public ResponseEntity<List<SubscriptionDTO>> byMentee(@PathVariable Long menteeId) {
        return new ResponseEntity<>(service.byMentee(menteeId), HttpStatus.OK);
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<SubscriptionDTO>> byMentor(@PathVariable Long mentorId) {
        return new ResponseEntity<>(service.byMentor(mentorId), HttpStatus.OK);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<SubscriptionDTO> updateStatus(@PathVariable Long id, @RequestParam SubscriptionStatus status) throws PortalException {
        return new ResponseEntity<>(service.updateStatus(id, status), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteNotSupported(@PathVariable Long id) {
        // For safety in MVP; subscriptions typically not hard-deleted.
        return new ResponseEntity<>(new ResponseDTO("Hard delete not supported"), HttpStatus.METHOD_NOT_ALLOWED);
    }
}
