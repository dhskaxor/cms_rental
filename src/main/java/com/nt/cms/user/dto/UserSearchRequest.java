package com.nt.cms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 검색 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {

    /**
     * 검색 키워드 (아이디, 이름, 이메일)
     */
    private String keyword;

    /**
     * 역할 ID
     */
    private Long roleId;

    /**
     * 상태 (ACTIVE, LOCKED)
     */
    private String status;

    /**
     * 페이지 번호 (1부터 시작)
     */
    @Builder.Default
    private int page = 1;

    /**
     * 페이지 크기
     */
    @Builder.Default
    private int size = 10;

    /**
     * offset 계산
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}
