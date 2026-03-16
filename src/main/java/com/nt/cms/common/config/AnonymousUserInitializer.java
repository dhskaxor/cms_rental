package com.nt.cms.common.config;

import com.nt.cms.common.config.CmsProperties;
import com.nt.cms.common.constant.SiteConstant;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.nt.cms.common.constant.SiteConstant.ROLE_ANONYMOUS;
import static com.nt.cms.common.constant.SiteConstant.USER_ANONYMOUS;

/**
 * 비회원(ANONYMOUS) 역할 및 익명 시스템 계정 초기화
 *
 * <p>기존 설치 환경에서 비회원 게시판 권한을 사용할 수 있도록, ANONYMOUS 역할과
 * anonymous 시스템 계정이 없으면 자동으로 생성한다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class AnonymousUserInitializer implements ApplicationRunner {

    private final CmsProperties cmsProperties;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!cmsProperties.isInstalled()) {
            return;
        }

        try {
            ensureAnonymousRole();
            ensureAnonymousUser();
        } catch (Exception e) {
            log.warn("비회원 역할/계정 초기화 중 오류 (무시 가능): {}", e.getMessage());
        }
    }

    /**
     * ANONYMOUS 역할이 없으면 생성
     */
    private void ensureAnonymousRole() {
        if (roleMapper.existsByRoleCode(ROLE_ANONYMOUS)) {
            return;
        }
        RoleVO role = RoleVO.builder()
                .roleCode(ROLE_ANONYMOUS)
                .roleName("비회원")
                .description("로그인하지 않은 사용자 (게시판 권한 설정용)")
                .build();
        roleMapper.insert(role);
        log.info("비회원(ANONYMOUS) 역할 생성 완료");
    }

    /**
     * anonymous 시스템 계정이 없으면 생성
     */
    private void ensureAnonymousUser() {
        if (userMapper.findByUsername(USER_ANONYMOUS) != null) {
            return;
        }
        RoleVO anonymousRole = roleMapper.findByRoleCode(ROLE_ANONYMOUS);
        if (anonymousRole == null) {
            return;
        }
        UserVO user = UserVO.builder()
                .username(USER_ANONYMOUS)
                .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                .name("비회원")
                .email("anonymous@system.local")
                .roleId(anonymousRole.getId())
                .status("ACTIVE")
                .build();
        userMapper.insert(user);
        log.info("비회원(anonymous) 시스템 계정 생성 완료");
    }
}
