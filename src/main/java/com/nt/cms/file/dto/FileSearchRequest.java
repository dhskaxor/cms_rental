package com.nt.cms.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 파일 검색 요청 DTO (관리자 목록용)
 *
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchRequest {

    /**
     * 참조 타입 (예: POST, GENERAL)
     */
    private String refType;

    /**
     * 참조 ID
     */
    private Long refId;

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
     * offset 계산
     */
    public int getOffset() {
        return ((page != null ? page : 1) - 1) * (size != null ? size : 20);
    }
}
