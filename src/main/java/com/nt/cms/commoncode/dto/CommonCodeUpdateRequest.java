package com.nt.cms.commoncode.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 코드 수정 요청 DTO
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeUpdateRequest {

    /**
     * 코드명
     */
    @Size(max = 200, message = "코드명은 200자 이하로 입력해주세요.")
    private String codeName;

    /**
     * 실제 저장 값
     */
    @Size(max = 500, message = "코드 값은 500자 이하로 입력해주세요.")
    private String codeValue;

    /**
     * 정렬 순서
     */
    private Integer sortOrder;

    /**
     * 사용 여부
     */
    private Boolean isActive;

    /**
     * 사용 시작일
     */
    private LocalDateTime startAt;

    /**
     * 사용 종료일
     */
    private LocalDateTime endAt;
}
