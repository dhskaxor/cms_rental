package com.nt.cms.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 * 
 * <p>시스템에서 발생하는 모든 에러 코드를 enum으로 관리한다.</p>
 * <p>권한 문자열 하드코딩 금지 규칙에 따라 상수로 관리</p>
 * 
 * @author CMS Team
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 공통 에러 (1000번대) =====
    INTERNAL_SERVER_ERROR("COMMON_1000", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT_VALUE("COMMON_1001", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_TYPE_VALUE("COMMON_1002", "입력값의 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("COMMON_1003", "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND("COMMON_1004", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // ===== 인증 에러 (2000번대) =====
    UNAUTHORIZED("AUTH_2000", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AUTH_2001", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AUTH_2002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("AUTH_2003", "유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    LOGIN_FAILED("AUTH_2004", "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("AUTH_2005", "계정이 잠겼습니다. 관리자에게 문의하세요.", HttpStatus.FORBIDDEN),
    
    // ===== 권한 에러 (2100번대) =====
    ACCESS_DENIED("AUTH_2100", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    PERMISSION_DENIED("AUTH_2101", "해당 작업을 수행할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    
    // ===== 사용자 에러 (3000번대) =====
    USER_NOT_FOUND("USER_3000", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_3001", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("USER_3002", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_POLICY_VIOLATION("USER_3003", "비밀번호 정책을 충족하지 않습니다. (영문/숫자/특수문자 포함, 6~12자)", HttpStatus.BAD_REQUEST),
    DUPLICATE_USERNAME("USER_3004", "이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("USER_3005", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD_RESET_TOKEN("USER_3006", "유효하지 않거나 만료된 비밀번호 재설정 링크입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH("USER_3007", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("USER_3008", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("USER_3009", "현재 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_SELF("USER_3010", "자기 자신은 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 역할 에러 (3100번대) =====
    ROLE_NOT_FOUND("ROLE_3100", "역할을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS("ROLE_3101", "이미 존재하는 역할입니다.", HttpStatus.CONFLICT),
    ROLE_IN_USE("ROLE_3102", "사용 중인 역할은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    
    // ===== 권한(Permission) 에러 (3200번대) =====
    PERMISSION_NOT_FOUND("PERM_3200", "권한을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PERMISSION_ALREADY_EXISTS("PERM_3201", "이미 존재하는 권한입니다.", HttpStatus.CONFLICT),
    PERMISSION_IN_USE("PERM_3202", "사용 중인 권한은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    
    // ===== 게시판 에러 (4000번대) =====
    BOARD_NOT_FOUND("BOARD_4000", "게시판을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BOARD_ALREADY_EXISTS("BOARD_4001", "이미 존재하는 게시판 코드입니다.", HttpStatus.CONFLICT),
    BOARD_GROUP_NOT_FOUND("BOARD_4002", "게시판 그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BOARD_GROUP_ALREADY_EXISTS("BOARD_4003", "이미 존재하는 그룹 코드입니다.", HttpStatus.CONFLICT),
    BOARD_GROUP_HAS_BOARDS("BOARD_4004", "게시판이 있는 그룹은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    BOARD_HAS_POSTS("BOARD_4005", "게시글이 있는 게시판은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    BOARD_PERMISSION_DENIED("BOARD_4006", "게시판 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    
    // ===== 게시글 에러 (4100번대) =====
    POST_NOT_FOUND("POST_4100", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    POST_ACCESS_DENIED("POST_4101", "게시글에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    SECRET_POST_ACCESS_DENIED("POST_4102", "비밀글은 작성자만 볼 수 있습니다.", HttpStatus.FORBIDDEN),
    
    // ===== 댓글 에러 (4200번대) =====
    COMMENT_NOT_FOUND("COMMENT_4200", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_DEPTH_EXCEEDED("COMMENT_4201", "대댓글은 1단계까지만 작성할 수 있습니다.", HttpStatus.BAD_REQUEST),
    COMMENT_DISABLED("COMMENT_4202", "해당 게시판은 댓글 기능이 비활성화되어 있습니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 파일 에러 (5000번대) =====
    FILE_NOT_FOUND("FILE_5000", "파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FILE_UPLOAD_FAILED("FILE_5001", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_SIZE_EXCEEDED("FILE_5002", "파일 크기가 제한을 초과했습니다. (최대 3MB)", HttpStatus.BAD_REQUEST),
    FILE_EXTENSION_NOT_ALLOWED("FILE_5003", "허용되지 않는 파일 확장자입니다.", HttpStatus.BAD_REQUEST),
    FILE_COUNT_EXCEEDED("FILE_5004", "파일 개수가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 메뉴 에러 (4300번대) =====
    MENU_NOT_FOUND("MENU_4300", "메뉴를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MENU_CODE_ALREADY_EXISTS("MENU_4301", "이미 존재하는 메뉴 코드입니다.", HttpStatus.CONFLICT),
    MENU_HAS_CHILDREN("MENU_4302", "하위 메뉴가 있는 메뉴는 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    MENU_INVALID_PARENT("MENU_4303", "유효하지 않은 상위 메뉴입니다.", HttpStatus.BAD_REQUEST),
    MENU_DEPTH_EXCEEDED("MENU_4304", "메뉴 깊이가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 페이지 에러 (4400번대) =====
    PAGE_NOT_FOUND("PAGE_4400", "페이지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PAGE_CODE_ALREADY_EXISTS("PAGE_4401", "이미 존재하는 페이지 코드입니다.", HttpStatus.CONFLICT),
    PAGE_IN_USE("PAGE_4402", "메뉴에서 사용 중인 페이지는 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    
    // ===== 감사 로그 에러 (4500번대) =====
    AUDIT_LOG_NOT_FOUND("AUDIT_4500", "감사 로그를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // ===== 공통 코드 에러 (4600번대) =====
    COMMON_CODE_GROUP_NOT_FOUND("CC_4600", "공통 코드 그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMON_CODE_GROUP_ALREADY_EXISTS("CC_4601", "이미 존재하는 코드 그룹입니다.", HttpStatus.CONFLICT),
    COMMON_CODE_GROUP_HAS_CODES("CC_4602", "코드가 있는 그룹은 삭제할 수 없습니다.", HttpStatus.CONFLICT),
    COMMON_CODE_GROUP_SYSTEM_RESTRICTED("CC_4603", "시스템 코드 그룹은 수정/삭제할 수 없습니다.", HttpStatus.FORBIDDEN),
    COMMON_CODE_NOT_FOUND("CC_4610", "공통 코드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMON_CODE_ALREADY_EXISTS("CC_4611", "같은 그룹 내에 이미 존재하는 코드입니다.", HttpStatus.CONFLICT),
    COMMON_CODE_SYSTEM_RESTRICTED("CC_4612", "시스템 코드는 수정/삭제할 수 없습니다.", HttpStatus.FORBIDDEN),
    COMMON_CODE_HAS_CHILDREN("CC_4613", "하위 코드가 있는 코드는 삭제할 수 없습니다.", HttpStatus.CONFLICT),

    // ===== 팝업 에러 (4700번대) =====
    POPUP_NOT_FOUND("POPUP_4700", "팝업을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    POPUP_CODE_ALREADY_EXISTS("POPUP_4701", "이미 존재하는 팝업 코드입니다.", HttpStatus.CONFLICT),

    // ===== 설치 에러 (6000번대) =====
    ALREADY_INSTALLED("INSTALL_6000", "이미 설치가 완료되었습니다.", HttpStatus.BAD_REQUEST),
    DATABASE_CONNECTION_FAILED("INSTALL_6001", "데이터베이스 연결에 실패했습니다.", HttpStatus.BAD_REQUEST),
    SCHEMA_EXECUTION_FAILED("INSTALL_6002", "스키마 실행에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    /**
     * 에러 코드
     */
    private final String code;

    /**
     * 에러 메시지
     */
    private final String message;

    /**
     * HTTP 상태 코드
     */
    private final HttpStatus httpStatus;
}
