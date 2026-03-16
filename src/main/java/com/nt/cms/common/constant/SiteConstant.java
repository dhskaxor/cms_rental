package com.nt.cms.common.constant;

/**
 * 사이트 관련 공통 상수
 * 
 * <p>익명 사용자, 에러 뷰 등 사이트 전반에서 사용되는 상수를 정의한다.</p>
 * 
 * @author CMS Team
 */
public final class SiteConstant {

    private SiteConstant() {
        // 인스턴스화 방지
    }

    /** 익명 사용자 역할 코드 */
    public static final String ROLE_ANONYMOUS = "ANONYMOUS";

    /** 익명 사용자 이름 */
    public static final String USER_ANONYMOUS = "anonymous";

    /** 사이트 에러 뷰 경로 */
    public static final String SITE_ERROR_VIEW = "site/error";
}
