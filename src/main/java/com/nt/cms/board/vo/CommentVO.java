package com.nt.cms.board.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 게시글 ID
     */
    private Long postId;

    /**
     * 부모 댓글 ID (대댓글인 경우)
     */
    private Long parentId;

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
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 삭제 여부
     */
    private Boolean deleted;

    /**
     * 대댓글 목록
     */
    private List<CommentVO> replies;

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return Boolean.TRUE.equals(this.deleted);
    }
}
