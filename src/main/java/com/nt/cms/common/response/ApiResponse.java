package com.nt.cms.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * API 표준 응답 포맷
 * 
 * <p>모든 API 응답은 이 클래스를 사용하여 일관된 형식으로 반환한다.</p>
 * 
 * <pre>
 * 성공 응답:
 * {
 *   "success": true,
 *   "data": { ... },
 *   "error": null
 * }
 * 
 * 에러 응답:
 * {
 *   "success": false,
 *   "data": null,
 *   "error": {
 *     "code": "ERROR_CODE",
 *     "message": "에러 메시지"
 *   }
 * }
 * </pre>
 * 
 * @param <T> 응답 데이터 타입
 * @author CMS Team
 */
@Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ApiResponse<T> {

    /**
     * 성공 여부
     */
    private final boolean success;

    /**
     * 응답 데이터 (성공 시)
     */
    private final T data;

    /**
     * 에러 정보 (실패 시)
     */
    private final ErrorResponse error;

    /**
     * 생성자
     * 
     * @param success 성공 여부
     * @param data 응답 데이터
     * @param error 에러 정보
     */
    private ApiResponse(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    /**
     * 성공 응답 생성 (데이터 포함)
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 성공 응답 생성 (데이터 없음)
     * 
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    /**
     * 에러 응답 생성
     * 
     * @param error 에러 정보
     * @param <T> 데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }

    /**
     * 에러 응답 생성 (코드와 메시지 직접 지정)
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param <T> 데이터 타입
     * @return 에러 응답
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message));
    }
}
