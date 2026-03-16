package com.nt.cms.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 최신글 조회 요청 DTO
 * 
 * <p>여러 게시판의 최신글을 조회할 때 사용하는 요청 객체</p>
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Schema(description = "최신글 조회 요청")
public class LatestPostsRequest {

    /**
     * 게시판 ID 목록 (필수)
     */
    @Schema(description = "게시판 ID 목록 (콤마 구분)", example = "1,2,3", required = true)
    private List<Long> boardIds;

    /**
     * 조회할 게시글 수 (기본값: 10, 최대: 50)
     */
    @Schema(description = "조회할 게시글 수", example = "10", defaultValue = "10")
    private Integer size = 10;

    /**
     * 정렬 필드 (createdAt, viewCount, title)
     */
    @Schema(description = "정렬 필드 (createdAt, viewCount, title)", example = "createdAt", defaultValue = "createdAt")
    private String sortField = "createdAt";

    /**
     * 정렬 방식 (ASC, DESC)
     */
    @Schema(description = "정렬 방식 (ASC, DESC)", example = "DESC", defaultValue = "DESC")
    private String sortOrder = "DESC";

    /**
     * 유효한 size 값 반환 (1~50 범위 제한)
     */
    public int getValidSize() {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, 50);
    }

    /**
     * 유효한 정렬 필드 반환 (허용된 값만)
     */
    public String getValidSortField() {
        if (sortField == null) {
            return "created_at";
        }
        switch (sortField.toLowerCase()) {
            case "viewcount":
                return "view_count";
            case "title":
                return "title";
            case "createdat":
            default:
                return "created_at";
        }
    }

    /**
     * 유효한 정렬 방식 반환 (ASC 또는 DESC)
     */
    public String getValidSortOrder() {
        if ("ASC".equalsIgnoreCase(sortOrder)) {
            return "ASC";
        }
        return "DESC";
    }
}
