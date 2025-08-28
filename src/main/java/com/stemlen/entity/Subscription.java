package com.stemlen.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.stemlen.dto.SubscriptionDTO;
import com.stemlen.dto.SubscriptionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "subscriptions")
public class Subscription {
    @Id
    private Long id;

    private Long menteeId;
    private Long mentorId;
    private Long packageId;

    private Integer planMonths; // 1, 3, 6, etc.

    private LocalDate startDate;
    private LocalDate endDate;

    // Pricing breakdown (all amounts are in the smallest currency unit depending on gateway decision; here we keep Double for simplicity)
    private Double basePricePerMonth;           // list price per month as set in package
    private Double planDiscountPercent;         // e.g., 0, 20, 40
    private Double checkoutDiscountPercent;     // e.g., 0 or 30
    private Double studentDiscountPercent;      // e.g., 0 or 5

    private Double effectivePricePerMonth;      // after all discounts
    private Double totalPriceBeforeDiscounts;   // basePricePerMonth * planMonths
    private Double totalDiscountAmount;         // total before - total payable
    private Double totalPayable;                // final amount for the whole plan tenure

    private SubscriptionStatus status;          // PENDING, ACTIVE, CANCELLED, REFUNDED, COMPLETED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SubscriptionDTO toDTO() {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(id);
        dto.setMenteeId(menteeId);
        dto.setMentorId(mentorId);
        dto.setPackageId(packageId);
        dto.setPlanMonths(planMonths);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setBasePricePerMonth(basePricePerMonth);
        dto.setPlanDiscountPercent(planDiscountPercent);
        dto.setCheckoutDiscountPercent(checkoutDiscountPercent);
        dto.setStudentDiscountPercent(studentDiscountPercent);
        dto.setEffectivePricePerMonth(effectivePricePerMonth);
        dto.setTotalPriceBeforeDiscounts(totalPriceBeforeDiscounts);
        dto.setTotalDiscountAmount(totalDiscountAmount);
        dto.setTotalPayable(totalPayable);
        dto.setStatus(status);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        return dto;
    }
}
