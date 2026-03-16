package com.nt.cms.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 감사 로그 검색 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogSearchRequest {

    /**
     * 페이지 번호 (1부터 시작)
     */
    @Builder.Default
    private Integer page = 1;

    /**
     * 페이지 크기
     */
    @Builder.Default
    private Integer size = 20;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 액션 타입 (CREATE, UPDATE, DELETE)
     */
    private String action;

    /**
     * 대상 타입 (USER, POST, BOARD 등)
     */
    private String targetType;

    /**
     * 대상 ID
     */
    private Long targetId;

    /**
     * 검색 시작일 (yyyy-MM-dd)
     */
    private String startDate;

    /**
     * 검색 종료일 (yyyy-MM-dd)
     */
    private String endDate;

    /**
     * offset 계산
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}
