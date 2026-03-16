package com.nt.cms.user.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 사용자 VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO extends BaseVO {

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
     * 역할 코드 (조인)
     */
    private String roleCode;

    /**
     * 역할명 (조인)
     */
    private String roleName;

    /**
     * 사용자 상태 (ACTIVE, LOCKED)
     */
    private String status;

    /**
     * 마지막 로그인 일시
     */
    private LocalDateTime lastLoginAt;

    /**
     * 로그인 실패 횟수 (캐시용, DB 미저장)
     */
    private transient int loginFailCount;

    /**
     * 계정 활성화 여부
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /**
     * 계정 잠금 여부
     */
    public boolean isLocked() {
        return "LOCKED".equals(status);
    }
}
