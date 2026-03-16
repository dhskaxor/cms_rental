package com.nt.cms.test.support;

import com.nt.cms.auth.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WithMockCustomUser용 SecurityContext 팩토리
 */
public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        List<GrantedAuthority> authorities = Arrays.stream(annotation.authorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .userId(annotation.userId())
                .username(annotation.username())
                .password("password")
                .name("테스트유저")
                .email("test@test.com")
                .roleId(1L)
                .roleCode("USER")
                .authorities(authorities)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()));
        return context;
    }
}
