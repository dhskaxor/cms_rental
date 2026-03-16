package com.nt.cms.board.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시판 권한 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPermissionRequest {

    /**
     * 역할 ID
     */
    @NotNull(message = "역할 ID를 입력해주세요.")
    private Long roleId;

    /**
     * 생성 권한
     */
    @Builder.Default
    private Boolean canCreate = false;

    /**
     * 조회 권한
     */
    @Builder.Default
    private Boolean canRead = true;

    /**
     * 수정 권한
     */
    @Builder.Default
    private Boolean canUpdate = false;

    /**
     * 삭제 권한
     */
    @Builder.Default
    private Boolean canDelete = false;
}
