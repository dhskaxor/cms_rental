package com.nt.cms.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    /**
     * 부모 댓글 ID (대댓글인 경우)
     */
    private Long parentId;

    /**
     * 댓글 내용
     */
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
