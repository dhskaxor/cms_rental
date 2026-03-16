package com.nt.cms.audit.dto;

import com.nt.cms.audit.vo.AuditLogVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 감사 로그 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    /**
     * 감사 로그 ID
     */
    private Long id;

    /**
     * 수행한 사용자 ID
     */
    private Long userId;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 액션 타입
     */
    private String action;

    /**
     * 대상 타입
     */
    private String targetType;

    /**
     * 대상 ID
     */
    private Long targetId;

    /**
     * 변경 전 데이터 (JSON)
     */
    private String beforeData;

    /**
     * 변경 후 데이터 (JSON)
     */
    private String afterData;

    /**
     * 클라이언트 IP
     */
    private String ipAddress;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * AuditLogVO로부터 응답 DTO 생성
     */
    public static AuditLogResponse from(AuditLogVO vo) {
        if (vo == null) {
            return null;
        }
        return AuditLogResponse.builder()
                .id(vo.getId())
                .userId(vo.getUserId())
                .username(vo.getUsername())
                .action(vo.getAction())
                .targetType(vo.getTargetType())
                .targetId(vo.getTargetId())
                .beforeData(vo.getBeforeData())
                .afterData(vo.getAfterData())
                .ipAddress(vo.getIpAddress())
                .createdAt(vo.getCreatedAt())
                .build();
    }
}
