package com.nt.cms.board.dto;

import com.nt.cms.board.vo.CommentVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private Long postId;
    private Long parentId;
    private String content;
    private Long writerId;
    private String writerName;
    private String writerUsername;
    private LocalDateTime createdAt;
    private Boolean deleted;
    private List<CommentResponse> replies;

    /**
     * VO에서 Response 생성
     */
    public static CommentResponse from(CommentVO vo) {
        if (vo == null) {
            return null;
        }

        List<CommentResponse> replyResponses = null;
        if (vo.getReplies() != null) {
            replyResponses = vo.getReplies().stream()
                    .map(CommentResponse::from)
                    .collect(Collectors.toList());
        }

        return CommentResponse.builder()
                .id(vo.getId())
                .postId(vo.getPostId())
                .parentId(vo.getParentId())
                .content(vo.isDeleted() ? "삭제된 댓글입니다." : vo.getContent())
                .writerId(vo.getWriterId())
                .writerName(vo.getWriterName())
                .writerUsername(vo.getWriterUsername())
                .createdAt(vo.getCreatedAt())
                .deleted(vo.getDeleted())
                .replies(replyResponses)
                .build();
    }
}
