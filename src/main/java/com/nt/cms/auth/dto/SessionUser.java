package com.nt.cms.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 세션 사용자 정보 (Admin/Site 공통)
 *
 * <p>관리자 웹 및 사용자 사이트 로그인 시 세션에 저장되는 사용자 정보.</p>
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 사용자 ID */
    private Long id;
    /** 로그인 아이디 */
    private String username;
    /** 이름 */
    private String name;
    /** 이메일 */
    private String email;
    /** 역할 코드 */
    private String roleCode;
}
