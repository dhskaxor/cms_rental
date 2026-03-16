package com.nt.cms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 초기화 요청 DTO
 *
 * @author CMS Team
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {

    /** 새 비밀번호 */
    private String newPassword;
}
