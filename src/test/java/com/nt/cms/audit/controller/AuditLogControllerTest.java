package com.nt.cms.audit.controller;

import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.common.exception.GlobalExceptionHandler;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.audit.dto.AuditLogResponse;
import com.nt.cms.audit.dto.AuditLogSearchRequest;
import com.nt.cms.audit.service.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuditLogController 테스트
 */
@WebMvcTest(AuditLogController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AuditLogController 테스트")
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Nested
    @DisplayName("감사 로그 목록 조회 API")
    class GetAuditLogsTest {

        @Test
        @DisplayName("목록 조회 성공")
        @WithMockUser(authorities = "AUDIT_READ")
        void getAuditLogs_success() throws Exception {
            AuditLogResponse response = AuditLogResponse.builder()
                    .id(1L)
                    .userId(1L)
                    .username("admin")
                    .action("UPDATE")
                    .targetType("USER")
                    .targetId(2L)
                    .createdAt(LocalDateTime.now())
                    .build();

            given(auditLogService.getAuditLogs(any(AuditLogSearchRequest.class)))
                    .willReturn(PageResponse.of(List.of(response), 1, 20, 1));

            mockMvc.perform(get("/api/v1/audit-logs")
                            .param("page", "1")
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].action").value("UPDATE"));
        }
    }

    @Nested
    @DisplayName("감사 로그 상세 조회 API")
    class GetAuditLogTest {

        @Test
        @DisplayName("상세 조회 성공")
        @WithMockUser(authorities = "AUDIT_READ")
        void getAuditLog_success() throws Exception {
            AuditLogResponse response = AuditLogResponse.builder()
                    .id(1L)
                    .action("DELETE")
                    .targetType("POST")
                    .targetId(10L)
                    .build();

            given(auditLogService.getAuditLog(1L)).willReturn(response);

            mockMvc.perform(get("/api/v1/audit-logs/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1));
        }
    }
}
