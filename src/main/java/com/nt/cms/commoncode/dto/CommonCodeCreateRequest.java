package com.nt.cms.commoncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 코드 생성 요청 DTO
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeCreateRequest {

    @NotNull(message = "코드 그룹을 선택해주세요.")
    private Long groupId;

    private Long parentId;

    @NotBlank(message = "코드를 입력해주세요.")
    @Size(max = 100)
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "코드는 영문, 숫자, 하이픈, 언더스코어만 허용됩니다.")
    private String code;

    @NotBlank(message = "코드명을 입력해주세요.")
    @Size(max = 200)
    private String codeName;

    @Size(max = 500)
    private String codeValue;

    private Integer sortOrder;
    private Boolean isActive;
    private Boolean isSystem;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
