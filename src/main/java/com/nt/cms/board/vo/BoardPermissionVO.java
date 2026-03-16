package com.nt.cms.board.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 게시판 권한 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardPermissionVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 게시판 ID
     */
    private Long boardId;

    /**
     * 역할 ID
     */
    private Long roleId;

    /**
     * 역할 코드 (조인)
     */
    private String roleCode;

    /**
     * 역할명 (조인)
     */
    private String roleName;

    /**
     * 생성 권한
     */
    private Boolean canCreate;

    /**
     * 조회 권한
     */
    private Boolean canRead;

    /**
     * 수정 권한
     */
    private Boolean canUpdate;

    /**
     * 삭제 권한
     */
    private Boolean canDelete;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;
}
