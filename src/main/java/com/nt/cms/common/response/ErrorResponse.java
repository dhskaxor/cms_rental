package com.nt.cms.common.response;

import com.nt.cms.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 에러 응답 정보
 * 
 * <p>API 에러 발생 시 반환되는 에러 정보를 담는 클래스</p>
 * 
 * <pre>
 * {
 *   "code": "USER_NOT_FOUND",
 *   "message": "사용자를 찾을 수 없습니다."
 * }
 * </pre>
 * 
 * @author CMS Team
 */
@Getter
public class ErrorResponse {

    /**
     * 에러 코드
     */
    private final String code;

    /**
     * 에러 메시지
     */
    private final String message;

    /**
     * 생성자
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     */
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * ErrorCode enum으로부터 생성
     * 
     * @param errorCode 에러 코드 enum
     */
    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * ErrorCode enum과 커스텀 메시지로 생성
     * 
     * @param errorCode 에러 코드 enum
     * @param customMessage 커스텀 메시지
     */
    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.code = errorCode.getCode();
        this.message = customMessage;
    }
}
