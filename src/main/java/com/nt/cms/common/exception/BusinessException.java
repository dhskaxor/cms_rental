package com.nt.cms.common.exception;

import lombok.Getter;

/**
 * 비즈니스 예외
 * 
 * <p>비즈니스 로직에서 발생하는 예외를 처리하기 위한 커스텀 예외 클래스</p>
 * <p>ErrorCode를 통해 일관된 에러 응답을 제공한다.</p>
 * 
 * @author CMS Team
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 에러 코드
     */
    private final ErrorCode errorCode;

    /**
     * 커스텀 메시지 (null이면 ErrorCode의 기본 메시지 사용)
     */
    private final String customMessage;

    /**
     * ErrorCode로 예외 생성
     * 
     * @param errorCode 에러 코드
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    /**
     * ErrorCode와 커스텀 메시지로 예외 생성
     * 
     * @param errorCode 에러 코드
     * @param customMessage 커스텀 메시지
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    /**
     * ErrorCode와 원인 예외로 예외 생성
     * 
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    /**
     * 실제 반환할 메시지 조회
     * 커스텀 메시지가 있으면 커스텀 메시지, 없으면 ErrorCode의 기본 메시지
     * 
     * @return 에러 메시지
     */
    public String getActualMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}
