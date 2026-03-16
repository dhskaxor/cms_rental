package com.nt.cms.rental.calendar.service;

import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleRequest;
import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleResponse;

import java.util.List;

public interface RentalClosedRuleService {

    List<RentalPlaceClosedRuleResponse> getRules(Long placeId, String fromDate, String toDate);

    void createRule(Long placeId, RentalPlaceClosedRuleRequest request, Long actorId);

    void updateRule(Long id, Long placeId, RentalPlaceClosedRuleRequest request, Long actorId);

    void deleteRule(Long id, Long placeId, Long actorId);
}

