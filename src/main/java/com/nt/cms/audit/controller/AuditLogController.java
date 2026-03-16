package com.nt.cms.audit.controller;

import com.nt.cms.audit.dto.AuditLogResponse;
import com.nt.cms.audit.dto.AuditLogSearchRequest;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 감사 로그 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "감사 로그", description = "감사 로그 조회 API")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 감사 로그 목록 조회 (페이징, 검색)
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).AUDIT_READ.value)")
    @Operation(summary = "감사 로그 목록 조회", description = "검색 조건에 따른 감사 로그 목록을 조회합니다.")
    public ApiResponse<PageResponse<AuditLogResponse>> getAuditLogs(
            @ModelAttribute AuditLogSearchRequest request) {
        PageResponse<AuditLogResponse> response = auditLogService.getAuditLogs(request);
        return ApiResponse.success(response);
    }

    /**
     * 감사 로그 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).AUDIT_READ.value)")
    @Operation(summary = "감사 로그 상세 조회", description = "감사 로그 ID로 상세 정보를 조회합니다.")
    public ApiResponse<AuditLogResponse> getAuditLog(@PathVariable Long id) {
        AuditLogResponse response = auditLogService.getAuditLog(id);
        return ApiResponse.success(response);
    }
}
