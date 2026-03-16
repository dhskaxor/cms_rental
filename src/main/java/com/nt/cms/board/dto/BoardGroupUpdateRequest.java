package com.nt.cms.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시판 그룹 수정 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardGroupUpdateRequest {

    /**
     * 그룹명
     */
    @NotBlank(message = "그룹명을 입력해주세요.")
    @Size(max = 100, message = "그룹명은 100자 이하로 입력해주세요.")
    private String groupName;
}
