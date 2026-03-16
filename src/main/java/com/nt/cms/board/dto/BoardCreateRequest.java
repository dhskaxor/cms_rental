package com.nt.cms.board.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 게시판 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    /**
     * 게시판 코드
     */
    @NotBlank(message = "게시판 코드를 입력해주세요.")
    @Size(max = 50, message = "게시판 코드는 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "게시판 코드는 영문 소문자로 시작하고, 영문 소문자/숫자/언더스코어만 허용됩니다.")
    private String boardCode;

    /**
     * 게시판명
     */
    @NotBlank(message = "게시판명을 입력해주세요.")
    @Size(max = 100, message = "게시판명은 100자 이하로 입력해주세요.")
    private String boardName;

    /**
     * 그룹 ID
     */
    private Long groupId;

    /**
     * 댓글 사용 여부
     */
    @Builder.Default
    private Boolean useComment = true;

    /**
     * 파일 사용 여부
     */
    @Builder.Default
    private Boolean useFile = true;

    /**
     * 최대 파일 수
     */
    @Min(value = 1, message = "최대 파일 수는 1 이상이어야 합니다.")
    @Max(value = 20, message = "최대 파일 수는 20 이하여야 합니다.")
    @Builder.Default
    private Integer maxFileCount = 5;

    /**
     * 비밀글 사용 여부
     */
    @Builder.Default
    private Boolean useSecret = false;

    /**
     * 페이지 크기
     */
    @Min(value = 5, message = "페이지 크기는 5 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
    @Builder.Default
    private Integer pageSize = 10;

    /**
     * 사용자 사이트 게시판 템플릿 코드 (default, card 등)
     */
    @Size(max = 50, message = "템플릿 코드는 50자 이하로 입력해주세요.")
    @Builder.Default
    private String templateCode = "default";

    /**
     * 리치 에디터(WYSIWYG) 사용 여부
     */
    @Builder.Default
    private Boolean useEditor = false;

    /**
     * 권한 설정 목록
     */
    private List<BoardPermissionRequest> permissions;
}
