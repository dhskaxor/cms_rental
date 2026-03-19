package com.nt.cms.siteconfig.constant;

import java.util.Set;

/**
 * 사용자 사이트 기본 테마 키 상수
 */
public final class SiteTheme {

    private SiteTheme() {
    }

    public static final String DARK = "dark";
    public static final String LIGHT = "light";
    public static final String SKY = "sky";
    public static final String CLASSIC = "classic";

    private static final Set<String> SUPPORTED_THEMES = Set.of(DARK, LIGHT, SKY, CLASSIC);

    /**
     * 입력 테마 키를 정규화한다.
     * 지원하지 않는 값 또는 null/blank는 dark로 폴백한다.
     */
    public static String normalize(String value) {
        if (value == null) {
            return DARK;
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return DARK;
        }
        return SUPPORTED_THEMES.contains(normalized) ? normalized : DARK;
    }
}
