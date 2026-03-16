package com.nt.cms.auth.security;

import com.nt.cms.auth.mapper.AuthMapper;
import com.nt.cms.auth.vo.UserAuthVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security UserDetailsService 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 인증 정보 조회: {}", username);
        
        UserAuthVO user = authMapper.findByUsername(username);
        
        if (user == null) {
            log.warn("사용자를 찾을 수 없습니다: {}", username);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 사용자 상태 확인
        if (user.isLocked()) {
            log.warn("계정이 잠금 상태입니다: {}", username);
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 권한 정보 조회
        List<String> permissionCodes = authMapper.findPermissionsByRoleId(user.getRoleId());
        List<GrantedAuthority> authorities = buildAuthorities(user.getRoleCode(), permissionCodes);

        return CustomUserDetails.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .name(user.getName())
                .email(user.getEmail())
                .roleId(user.getRoleId())
                .roleCode(user.getRoleCode())
                .authorities(authorities)
                .enabled(user.isActive())
                .accountNonLocked(!user.isLocked())
                .build();
    }

    /**
     * 사용자 ID로 UserDetails 조회
     * 
     * @param userId 사용자 ID
     * @return UserDetails
     */
    public UserDetails loadUserById(Long userId) {
        log.debug("사용자 인증 정보 조회 (ID): {}", userId);
        
        UserAuthVO user = authMapper.findById(userId);
        
        if (user == null) {
            log.warn("사용자를 찾을 수 없습니다 (ID): {}", userId);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }

        List<String> permissionCodes = authMapper.findPermissionsByRoleId(user.getRoleId());
        List<GrantedAuthority> authorities = buildAuthorities(user.getRoleCode(), permissionCodes);

        return CustomUserDetails.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .name(user.getName())
                .email(user.getEmail())
                .roleId(user.getRoleId())
                .roleCode(user.getRoleCode())
                .authorities(authorities)
                .enabled(user.isActive())
                .accountNonLocked(!user.isLocked())
                .build();
    }

    /**
     * 권한 목록 생성
     * 
     * @param roleCode 역할 코드
     * @param permissionCodes 권한 코드 목록
     * @return GrantedAuthority 목록
     */
    private List<GrantedAuthority> buildAuthorities(String roleCode, List<String> permissionCodes) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 역할을 ROLE_ 접두어로 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
        
        // 권한 코드 추가
        if (permissionCodes != null) {
            authorities.addAll(
                permissionCodes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList())
            );
        }
        
        return authorities;
    }
}
