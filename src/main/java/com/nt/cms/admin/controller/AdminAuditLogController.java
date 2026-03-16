package com.nt.cms.admin.controller;

import com.nt.cms.audit.dto.AuditLogResponse;
import com.nt.cms.audit.dto.AuditLogSearchRequest;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 감사 로그 웹 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/audit-logs")
    public String auditLogs(@ModelAttribute AuditLogSearchRequest request, Model model) {
        if (request.getPage() == null) request.setPage(1);
        if (request.getSize() == null) request.setSize(20);
        PageResponse<AuditLogResponse> pageResponse = auditLogService.getAuditLogs(request);
        model.addAttribute("auditLogs", pageResponse.getContent());
        model.addAttribute("page", pageResponse.getPage());
        model.addAttribute("size", pageResponse.getSize());
        model.addAttribute("totalElements", pageResponse.getTotalElements());
        model.addAttribute("totalPages", pageResponse.getTotalPages());
        model.addAttribute("request", request);
        model.addAttribute("currentMenu", "audit");
        model.addAttribute("pageTitle", "감사 로그");
        return "admin/audit-logs";
    }

    @GetMapping("/audit-logs/{id}")
    public String auditLogDetail(@PathVariable Long id, Model model) {
        var auditLog = auditLogService.getAuditLog(id);
        model.addAttribute("auditLog", auditLog);
        model.addAttribute("currentMenu", "audit");
        model.addAttribute("pageTitle", "감사 로그 상세");
        return "admin/audit-log-detail";
    }
}
