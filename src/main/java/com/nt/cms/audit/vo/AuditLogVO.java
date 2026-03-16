package com.nt.cms.audit.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 감사 로그 VO
 * 
 * <p>audit_log 테이블 매핑 객체. 수정/삭제 이력 추적용.</p>
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogVO {

    /**
     * Primary Key
     */
    private Long id;

    /**
     * 수행한 사용자 ID
     */
    private Long userId;

    /**
     * 사용자명 (비정규화 저장)
     */
    private String username;

    /**
     * 액션 타입 (CREATE, UPDATE, DELETE)
     */
    private String action;

    /**
     * 대상 엔티티 타입 (USER, POST, BOARD 등)
     */
    private String targetType;

    /**
     * 대상 엔티티 ID
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
     * User Agent
     */
    private String userAgent;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;
}
