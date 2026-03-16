package com.nt.cms.common.constant;

/**
 * 권한 코드 상수
 * DB permission.permission_code와 일치해야 한다.
 *
 * @author CMS Team
 */
public enum Permission {

    // 사용자
    USER_CREATE("USER_CREATE"),
    USER_READ("USER_READ"),
    USER_UPDATE("USER_UPDATE"),
    USER_DELETE("USER_DELETE"),

    // 역할
    ROLE_CREATE("ROLE_CREATE"),
    ROLE_READ("ROLE_READ"),
    ROLE_UPDATE("ROLE_UPDATE"),
    ROLE_DELETE("ROLE_DELETE"),

    // 게시판
    BOARD_CREATE("BOARD_CREATE"),
    BOARD_READ("BOARD_READ"),
    BOARD_UPDATE("BOARD_UPDATE"),
    BOARD_DELETE("BOARD_DELETE"),

    // 게시글
    POST_CREATE("POST_CREATE"),
    POST_READ("POST_READ"),
    POST_UPDATE("POST_UPDATE"),
    POST_DELETE("POST_DELETE"),

    // 댓글
    COMMENT_CREATE("COMMENT_CREATE"),
    COMMENT_READ("COMMENT_READ"),
    COMMENT_UPDATE("COMMENT_UPDATE"),
    COMMENT_DELETE("COMMENT_DELETE"),

    // 파일
    FILE_CREATE("FILE_CREATE"),
    FILE_READ("FILE_READ"),
    FILE_DELETE("FILE_DELETE"),

    // 감사 로그
    AUDIT_READ("AUDIT_READ"),

    // 공통 코드
    COMMON_CODE_CREATE("COMMON_CODE_CREATE"),
    COMMON_CODE_READ("COMMON_CODE_READ"),
    COMMON_CODE_UPDATE("COMMON_CODE_UPDATE"),
    COMMON_CODE_DELETE("COMMON_CODE_DELETE"),

    // 팝업
    POPUP_CREATE("POPUP_CREATE"),
    POPUP_READ("POPUP_READ"),
    POPUP_UPDATE("POPUP_UPDATE"),
    POPUP_DELETE("POPUP_DELETE"),

    // 메뉴
    MENU_CREATE("MENU_CREATE"),
    MENU_READ("MENU_READ"),
    MENU_UPDATE("MENU_UPDATE"),
    MENU_DELETE("MENU_DELETE"),

    // 페이지
    PAGE_CREATE("PAGE_CREATE"),
    PAGE_READ("PAGE_READ"),
    PAGE_UPDATE("PAGE_UPDATE"),
    PAGE_DELETE("PAGE_DELETE"),

    // 사이트 설정
    SITE_READ("SITE_READ"),
    SITE_UPDATE("SITE_UPDATE");

    private final String value;

    Permission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
