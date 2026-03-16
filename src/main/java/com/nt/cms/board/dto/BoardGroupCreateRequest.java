package com.nt.cms.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시판 그룹 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardGroupCreateRequest {

    /**
     * 그룹 코드
     */
    @NotBlank(message = "그룹 코드를 입력해주세요.")
    @Size(max = 50, message = "그룹 코드는 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "그룹 코드는 영문 소문자로 시작하고, 영문 소문자/숫자/언더스코어만 허용됩니다.")
    private String groupCode;

    /**
     * 그룹명
     */
    @NotBlank(message = "그룹명을 입력해주세요.")
    @Size(max = 100, message = "그룹명은 100자 이하로 입력해주세요.")
    private String groupName;
}
