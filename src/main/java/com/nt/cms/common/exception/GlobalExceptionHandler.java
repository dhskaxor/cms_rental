package com.nt.cms.common.exception;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 * 
 * <p>모든 컨트롤러에서 발생하는 예외를 일관된 형식으로 처리한다.</p>
 * <p>비즈니스 예외와 시스템 예외를 분리하여 처리한다.</p>
 * 
 * @author CMS Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     * 
     * @param e 비즈니스 예외
     * @return 에러 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: {} - {}", e.getErrorCode().getCode(), e.getActualMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().getCode(), e.getActualMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 유효성 검증 실패 예외 처리 (RequestBody)
     * 
     * @param e 유효성 검증 예외
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));
        
        log.warn("Validation failed: {}", errorMessage);
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE.getCode(), errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 유효성 검증 실패 예외 처리 (ModelAttribute)
     * 
     * @param e 바인딩 예외
     * @return 에러 응답
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));
        
        log.warn("Binding failed: {}", errorMessage);
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE.getCode(), errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 요청 파라미터 누락 예외 처리
     * 
     * @param e 파라미터 누락 예외
     * @return 에러 응답
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String errorMessage = String.format("필수 파라미터 '%s'가 누락되었습니다.", e.getParameterName());
        
        log.warn("Missing parameter: {}", e.getParameterName());
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE.getCode(), errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 타입 불일치 예외 처리
     * 
     * @param e 타입 불일치 예외
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage = String.format("파라미터 '%s'의 타입이 올바르지 않습니다.", e.getName());
        
        log.warn("Type mismatch: {} - expected {}", e.getName(), e.getRequiredType());
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE.getCode(), errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * HTTP 메서드 불일치 예외 처리
     * 
     * @param e HTTP 메서드 불일치 예외
     * @return 에러 응답
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not allowed: {}", e.getMethod());
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * JSON 파싱 실패 예외 처리
     * 
     * @param e JSON 파싱 예외
     * @return 에러 응답
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Message not readable: {}", e.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE.getCode(), "요청 본문을 파싱할 수 없습니다.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 404 Not Found 예외 처리
     * 
     * @param e 핸들러 없음 예외
     * @return 에러 응답
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("No handler found: {} {}", e.getHttpMethod(), e.getRequestURL());
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.RESOURCE_NOT_FOUND);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 인증 실패 예외 처리
     * 
     * @param e 인증 예외
     * @return 에러 응답
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.UNAUTHORIZED);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 권한 부족 예외 처리
     * 
     * @param e 접근 거부 예외
     * @return 에러 응답
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.ACCESS_DENIED);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 기타 모든 예외 처리 (시스템 예외)
     * 
     * @param e 예외
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error occurred", e);
        
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorResponse));
    }

    /**
     * 필드 에러 메시지 포맷팅
     * 
     * @param fieldError 필드 에러
     * @return 포맷팅된 메시지
     */
    private String formatFieldError(FieldError fieldError) {
        return String.format("[%s] %s", fieldError.getField(), fieldError.getDefaultMessage());
    }
}
