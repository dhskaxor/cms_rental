package com.nt.cms.auth.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Spring Security UserDetails 구현체
 * 
 * @author CMS Team
 */
@Getter
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    /**
     * 사용자 ID
     */
    private final Long userId;

    /**
     * 로그인 아이디
     */
    private final String username;

    /**
     * 암호화된 비밀번호
     */
    private final String password;

    /**
     * 사용자 이름
     */
    private final String name;

    /**
     * 이메일
     */
    private final String email;

    /**
     * 역할 ID
     */
    private final Long roleId;

    /**
     * 역할 코드
     */
    private final String roleCode;

    /**
     * 권한 목록
     */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 계정 활성화 여부
     */
    @Builder.Default
    private final boolean enabled = true;

    /**
     * 계정 만료 여부 (true = 만료되지 않음)
     */
    @Builder.Default
    private final boolean accountNonExpired = true;

    /**
     * 계정 잠금 여부 (true = 잠금되지 않음)
     */
    @Builder.Default
    private final boolean accountNonLocked = true;

    /**
     * 자격 증명 만료 여부 (true = 만료되지 않음)
     */
    @Builder.Default
    private final boolean credentialsNonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
