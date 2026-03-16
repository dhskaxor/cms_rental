package com.nt.cms.board.dto;

import com.nt.cms.board.vo.PostVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private Long boardId;
    private String boardCode;
    private String boardName;
    private String title;
    private String content;
    private Long writerId;
    private String writerName;
    private String writerUsername;
    private Integer viewCount;
    private Boolean isNotice;
    private Boolean isSecret;
    private Integer commentCount;
    private Integer fileCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * VO에서 Response 생성
     */
    public static PostResponse from(PostVO vo) {
        if (vo == null) {
            return null;
        }
        return PostResponse.builder()
                .id(vo.getId())
                .boardId(vo.getBoardId())
                .boardCode(vo.getBoardCode())
                .boardName(vo.getBoardName())
                .title(vo.getTitle())
                .content(vo.getContent())
                .writerId(vo.getWriterId())
                .writerName(vo.getWriterName())
                .writerUsername(vo.getWriterUsername())
                .viewCount(vo.getViewCount())
                .isNotice(vo.getIsNotice())
                .isSecret(vo.getIsSecret())
                .commentCount(vo.getCommentCount())
                .fileCount(vo.getFileCount())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }

    /**
     * 목록용 Response 생성 (내용 제외)
     */
    public static PostResponse fromList(PostVO vo) {
        if (vo == null) {
            return null;
        }
        return PostResponse.builder()
                .id(vo.getId())
                .boardId(vo.getBoardId())
                .boardCode(vo.getBoardCode())
                .boardName(vo.getBoardName())
                .title(vo.getTitle())
                .writerId(vo.getWriterId())
                .writerName(vo.getWriterName())
                .writerUsername(vo.getWriterUsername())
                .viewCount(vo.getViewCount())
                .isNotice(vo.getIsNotice())
                .isSecret(vo.getIsSecret())
                .commentCount(vo.getCommentCount())
                .fileCount(vo.getFileCount())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }
}
