package com.nt.cms.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 게시글 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    /**
     * 제목
     */
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")
    private String title;

    /**
     * 내용
     */
    private String content;

    /**
     * 공지 여부
     */
    @Builder.Default
    private Boolean isNotice = false;

    /**
     * 비밀글 여부
     */
    @Builder.Default
    private Boolean isSecret = false;

    /**
     * 에디터 인라인 이미지로 업로드된 파일 ID 목록 (refId=0 → postId로 갱신용)
     */
    private List<Long> attachedFileIds;
}
