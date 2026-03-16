package com.nt.cms.board.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 게시글 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostVO extends BaseVO {

    /**
     * 게시판 ID
     */
    private Long boardId;

    /**
     * 게시판 코드 (조인)
     */
    private String boardCode;

    /**
     * 게시판명 (조인)
     */
    private String boardName;

    /**
     * 제목
     */
    private String title;

    /**
     * 내용
     */
    private String content;

    /**
     * 작성자 ID
     */
    private Long writerId;

    /**
     * 작성자명 (조인)
     */
    private String writerName;

    /**
     * 작성자 사용자명 (조인)
     */
    private String writerUsername;

    /**
     * 조회수
     */
    private Integer viewCount;

    /**
     * 공지 여부
     */
    private Boolean isNotice;

    /**
     * 비밀글 여부
     */
    private Boolean isSecret;

    /**
     * 댓글 수 (집계)
     */
    private Integer commentCount;

    /**
     * 파일 수 (집계)
     */
    private Integer fileCount;
}
