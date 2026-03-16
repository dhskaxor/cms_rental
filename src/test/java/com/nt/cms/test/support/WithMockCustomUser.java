package com.nt.cms.test.support;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 테스트용 CustomUserDetails 시큐리티 컨텍스트
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long userId() default 1L;
    String username() default "testuser";
    String[] authorities() default {"FILE_DELETE"};
}
