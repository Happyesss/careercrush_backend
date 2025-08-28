package com.stemlen.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.stemlen.entity.Subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private Long menteeId;
    private Long mentorId;
    private Long packageId;
    private Integer planMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double basePricePerMonth;
    private Double planDiscountPercent;
    private Double checkoutDiscountPercent;
    private Double studentDiscountPercent;
    private Double effectivePricePerMonth;
    private Double totalPriceBeforeDiscounts;
    private Double totalDiscountAmount;
    private Double totalPayable;
    private SubscriptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Subscription toEntity() {
        Subscription e = new Subscription();
        e.setId(id);
        e.setMenteeId(menteeId);
        e.setMentorId(mentorId);
        e.setPackageId(packageId);
        e.setPlanMonths(planMonths);
        e.setStartDate(startDate);
        e.setEndDate(endDate);
        e.setBasePricePerMonth(basePricePerMonth);
        e.setPlanDiscountPercent(planDiscountPercent);
        e.setCheckoutDiscountPercent(checkoutDiscountPercent);
        e.setStudentDiscountPercent(studentDiscountPercent);
        e.setEffectivePricePerMonth(effectivePricePerMonth);
        e.setTotalPriceBeforeDiscounts(totalPriceBeforeDiscounts);
        e.setTotalDiscountAmount(totalDiscountAmount);
        e.setTotalPayable(totalPayable);
        e.setStatus(status);
        e.setCreatedAt(createdAt);
        e.setUpdatedAt(updatedAt);
        return e;
    }
}
