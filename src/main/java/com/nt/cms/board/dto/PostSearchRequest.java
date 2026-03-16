package com.nt.cms.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 게시글 검색 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchRequest {

    /**
     * 페이지 번호 (1부터 시작)
     */
    @Builder.Default
    private Integer page = 1;

    /**
     * 페이지 크기
     */
    @Builder.Default
    private Integer size = 10;

    /**
     * 검색 유형 (title, content, writer, all)
     */
    private String searchType;

    /**
     * 검색어
     */
    private String keyword;

    /**
     * 시작일 (yyyy-MM-dd)
     */
    private String startDate;

    /**
     * 종료일 (yyyy-MM-dd)
     */
    private String endDate;

    /**
     * offset 계산
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}
