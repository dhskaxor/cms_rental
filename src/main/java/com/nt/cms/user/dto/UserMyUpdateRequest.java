package com.nt.cms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 정보 수정 요청 DTO (사용자용)
 *
 * <p>관리자용 UserUpdateRequest와 달리 role/status는 변경하지 않는다.</p>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMyUpdateRequest {

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 100, message = "이름은 100자 이하로 입력해주세요.")
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 150, message = "이메일은 150자 이하로 입력해주세요.")
    private String email;
}

