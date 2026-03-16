package com.nt.cms.common.constant;

/**
 * 세션 관련 상수
 *
 * @author CMS Team
 */
public final class SessionConstants {

    /** 관리자 웹 로그인 사용자 세션 키 */
    public static final String CURRENT_USER = "currentUser";

    /** 사용자 사이트 로그인 사용자 세션 키 (Admin과 분리) */
    public static final String SITE_CURRENT_USER = "siteCurrentUser";

    private SessionConstants() {
    }
}
