package com.nt.cms.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 인증 정보 VO
 * 인증 처리에 필요한 사용자 정보를 담는 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthVO {

    /**
     * 사용자 ID
     */
    private Long id;

    /**
     * 로그인 아이디
     */
    private String username;

    /**
     * 암호화된 비밀번호
     */
    private String password;

    /**
     * 사용자 이름
     */
    private String name;

    /**
     * 이메일
     */
    private String email;

    /**
     * 역할 ID
     */
    private Long roleId;

    /**
     * 역할 코드 (ADMIN, MANAGER, USER)
     */
    private String roleCode;

    /**
     * 사용자 상태 (ACTIVE, LOCKED)
     */
    private String status;

    /**
     * 마지막 로그인 일시
     */
    private LocalDateTime lastLoginAt;

    /**
     * 삭제 여부
     */
    private Boolean deleted;

    /**
     * 보유 권한 코드 목록
     */
    private List<String> permissions;

    /**
     * 계정 활성화 여부 확인
     * 
     * @return 활성화되었으면 true
     */
    public boolean isActive() {
        return "ACTIVE".equals(status) && !Boolean.TRUE.equals(deleted);
    }

    /**
     * 계정 잠금 여부 확인
     * 
     * @return 잠금 상태이면 true
     */
    public boolean isLocked() {
        return "LOCKED".equals(status);
    }
}
