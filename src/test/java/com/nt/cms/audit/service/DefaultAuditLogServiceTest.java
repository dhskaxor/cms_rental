package com.nt.cms.audit.service;

import com.nt.cms.audit.dto.AuditLogResponse;
import com.nt.cms.audit.dto.AuditLogSearchRequest;
import com.nt.cms.audit.mapper.AuditLogMapper;
import com.nt.cms.audit.vo.AuditLogVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * DefaultAuditLogService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultAuditLogService 테스트")
class DefaultAuditLogServiceTest {

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private DefaultAuditLogService auditLogService;

    @Nested
    @DisplayName("감사 로그 기록")
    class LogTest {

        @Test
        @DisplayName("로그 기록 성공")
        void log_success() {
            auditLogService.log(1L, "admin", "UPDATE", "USER", 2L, "{}", "{\"name\":\"updated\"}");

            ArgumentCaptor<AuditLogVO> captor = ArgumentCaptor.forClass(AuditLogVO.class);
            verify(auditLogMapper).insert(captor.capture());
            assertThat(captor.getValue().getUserId()).isEqualTo(1L);
            assertThat(captor.getValue().getUsername()).isEqualTo("admin");
            assertThat(captor.getValue().getAction()).isEqualTo("UPDATE");
            assertThat(captor.getValue().getTargetType()).isEqualTo("USER");
            assertThat(captor.getValue().getTargetId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("감사 로그 목록 조회")
    class GetAuditLogsTest {

        @Test
        @DisplayName("목록 조회 성공")
        void getAuditLogs_success() {
            AuditLogVO vo = AuditLogVO.builder()
                    .id(1L)
                    .userId(1L)
                    .username("admin")
                    .action("UPDATE")
                    .targetType("USER")
                    .targetId(2L)
                    .createdAt(LocalDateTime.now())
                    .build();

            AuditLogSearchRequest request = AuditLogSearchRequest.builder()
                    .page(1)
                    .size(20)
                    .build();

            given(auditLogMapper.findAll(request)).willReturn(List.of(vo));
            given(auditLogMapper.countAll(request)).willReturn(1L);

            PageResponse<AuditLogResponse> response = auditLogService.getAuditLogs(request);

            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getAction()).isEqualTo("UPDATE");
            assertThat(response.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("감사 로그 상세 조회")
    class GetAuditLogTest {

        @Test
        @DisplayName("상세 조회 성공")
        void getAuditLog_success() {
            AuditLogVO vo = AuditLogVO.builder()
                    .id(1L)
                    .userId(1L)
                    .username("admin")
                    .action("DELETE")
                    .targetType("POST")
                    .targetId(10L)
                    .createdAt(LocalDateTime.now())
                    .build();

            given(auditLogMapper.findById(1L)).willReturn(vo);

            AuditLogResponse response = auditLogService.getAuditLog(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getAction()).isEqualTo("DELETE");
        }

        @Test
        @DisplayName("존재하지 않는 로그 조회 시 예외")
        void getAuditLog_notFound() {
            given(auditLogMapper.findById(999L)).willReturn(null);

            assertThatThrownBy(() -> auditLogService.getAuditLog(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUDIT_LOG_NOT_FOUND);
        }
    }
}
