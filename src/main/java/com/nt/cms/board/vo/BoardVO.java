package com.nt.cms.board.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 게시판 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BoardVO extends BaseVO {

    /**
     * 게시판 코드
     */
    private String boardCode;

    /**
     * 게시판명
     */
    private String boardName;

    /**
     * 그룹 ID
     */
    private Long groupId;

    /**
     * 그룹명 (조인)
     */
    private String groupName;

    /**
     * 댓글 사용 여부
     */
    private Boolean useComment;

    /**
     * 파일 사용 여부
     */
    private Boolean useFile;

    /**
     * 최대 파일 수
     */
    private Integer maxFileCount;

    /**
     * 비밀글 사용 여부
     */
    private Boolean useSecret;

    /**
     * 페이지 크기
     */
    private Integer pageSize;

    /**
     * 사용자 사이트 게시판 템플릿 코드 (default, card 등)
     */
    private String templateCode;

    /**
     * 리치 에디터(WYSIWYG) 사용 여부
     */
    private Boolean useEditor;

    /**
     * 게시글 수 (집계)
     */
    private Long postCount;

    /**
     * 게시판 권한 목록
     */
    private List<BoardPermissionVO> permissions;
}
