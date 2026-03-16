package com.nt.cms.rental.calendar.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleRequest;
import com.nt.cms.rental.calendar.dto.RentalPlaceClosedRuleResponse;
import com.nt.cms.rental.calendar.service.RentalClosedRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rental/places/{placeId}/closed-rules")
@RequiredArgsConstructor
public class AdminRentalClosedRuleController {

    private final RentalClosedRuleService rentalClosedRuleService;

    @GetMapping
    public ApiResponse<List<RentalPlaceClosedRuleResponse>> list(@PathVariable Long placeId,
                                                                 @RequestParam String fromDate,
                                                                 @RequestParam String toDate) {
        return ApiResponse.success(rentalClosedRuleService.getRules(placeId, fromDate, toDate));
    }

    @PostMapping
    public ApiResponse<Void> create(@PathVariable Long placeId,
                                    @RequestBody RentalPlaceClosedRuleRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalClosedRuleService.createRule(placeId, request, actorId);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long placeId,
                                    @PathVariable Long id,
                                    @RequestBody RentalPlaceClosedRuleRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalClosedRuleService.updateRule(id, placeId, request, actorId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long placeId,
                                    @PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        Long actorId = null;
        if (userDetails instanceof com.nt.cms.auth.security.CustomUserDetails custom) {
            actorId = custom.getUserId();
        }
        rentalClosedRuleService.deleteRule(id, placeId, actorId);
        return ApiResponse.success();
    }
}

