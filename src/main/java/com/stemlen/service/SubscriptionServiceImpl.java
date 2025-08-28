package com.stemlen.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stemlen.dto.SubscriptionDTO;
import com.stemlen.dto.SubscriptionStatus;
import com.stemlen.entity.MentorshipPackage;
import com.stemlen.entity.Subscription;
import com.stemlen.exception.PortalException;
import com.stemlen.repository.MentorshipPackageRepository;
import com.stemlen.repository.SubscriptionRepository;
import com.stemlen.utility.PricingUtil;
import com.stemlen.utility.Utilities;

@Service("subscriptionService")
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository repository;

    @Autowired
    private MentorshipPackageRepository packageRepository;

    @Override
    public SubscriptionDTO quote(Long mentorId, Long packageId, Integer planMonths, Double checkoutPercent, Double studentPercent) throws PortalException {
        MentorshipPackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new PortalException("PACKAGE_NOT_FOUND"));

        if (!pkg.getMentorId().equals(mentorId)) {
            throw new PortalException("PACKAGE_NOT_BELONG_TO_MENTOR");
        }

        double basePerMonth = pkg.getPricePerMonth() != null ? pkg.getPricePerMonth() : 0d;

        double planPercent = 0d;
        if (planMonths != null) {
            if (planMonths == 3) planPercent = 20d;
            else if (planMonths == 6) planPercent = 40d;
            else planPercent = 0d; // monthly or other
        }

        double coPercent = checkoutPercent != null ? checkoutPercent : 0d;
        double stuPercent = studentPercent != null ? studentPercent : 0d;

        double effPerMonth = PricingUtil.calculateEffectivePerMonth(basePerMonth, planPercent, coPercent, stuPercent);
        int months = planMonths != null ? planMonths : 1;
        double before = basePerMonth * months;
        double payable = PricingUtil.round2(effPerMonth * months);
        double discount = PricingUtil.round2(before - payable);

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setMentorId(mentorId);
        dto.setPackageId(packageId);
        dto.setPlanMonths(months);
        dto.setBasePricePerMonth(basePerMonth);
        dto.setPlanDiscountPercent(planPercent);
        dto.setCheckoutDiscountPercent(coPercent);
        dto.setStudentDiscountPercent(stuPercent);
        dto.setEffectivePricePerMonth(effPerMonth);
        dto.setTotalPriceBeforeDiscounts(before);
        dto.setTotalDiscountAmount(discount);
        dto.setTotalPayable(payable);
        dto.setStatus(SubscriptionStatus.PENDING);
        return dto;
    }

    @Override
    public SubscriptionDTO create(SubscriptionDTO dto) throws PortalException {
        if (dto.getId() == null || dto.getId() == 0) {
            dto.setId(Utilities.getNextSequence("subscriptions"));
            dto.setCreatedAt(LocalDateTime.now());
        }
        dto.setUpdatedAt(LocalDateTime.now());

        // derive end date if not provided
        if (dto.getStartDate() == null) dto.setStartDate(LocalDate.now());
        if (dto.getEndDate() == null && dto.getPlanMonths() != null) {
            dto.setEndDate(dto.getStartDate().plusMonths(dto.getPlanMonths()));
        }

        Subscription saved = repository.save(dto.toEntity());
        return saved.toDTO();
    }

    @Override
    public SubscriptionDTO get(Long id) throws PortalException {
        return repository.findById(id).orElseThrow(() -> new PortalException("SUBSCRIPTION_NOT_FOUND")).toDTO();
    }

    @Override
    public List<SubscriptionDTO> byMentee(Long menteeId) {
        return repository.findByMenteeId(menteeId).stream().map(Subscription::toDTO).toList();
    }

    @Override
    public List<SubscriptionDTO> byMentor(Long mentorId) {
        return repository.findByMentorId(mentorId).stream().map(Subscription::toDTO).toList();
    }

    @Override
    public SubscriptionDTO updateStatus(Long id, SubscriptionStatus status) throws PortalException {
        Subscription s = repository.findById(id).orElseThrow(() -> new PortalException("SUBSCRIPTION_NOT_FOUND"));
        s.setStatus(status);
        s.setUpdatedAt(LocalDateTime.now());
        return repository.save(s).toDTO();
    }
}
