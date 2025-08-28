package com.stemlen.service;

import java.util.List;

import com.stemlen.dto.SubscriptionDTO;
import com.stemlen.dto.SubscriptionStatus;
import com.stemlen.exception.PortalException;

public interface SubscriptionService {
    SubscriptionDTO quote(Long mentorId, Long packageId, Integer planMonths, Double checkoutPercent, Double studentPercent) throws PortalException;
    SubscriptionDTO create(SubscriptionDTO dto) throws PortalException;
    SubscriptionDTO get(Long id) throws PortalException;
    List<SubscriptionDTO> byMentee(Long menteeId);
    List<SubscriptionDTO> byMentor(Long mentorId);
    SubscriptionDTO updateStatus(Long id, SubscriptionStatus status) throws PortalException;
}
