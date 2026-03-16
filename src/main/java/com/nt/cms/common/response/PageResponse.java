package com.nt.cms.common.response;

import lombok.Getter;

import java.util.List;

/**
 * 페이징 응답 포맷
 * 
 * <p>목록 조회 API에서 페이징 정보와 함께 데이터를 반환할 때 사용</p>
 * 
 * <pre>
 * {
 *   "content": [...],
 *   "page": 1,
 *   "size": 10,
 *   "totalElements": 100,
 *   "totalPages": 10
 * }
 * </pre>
 * 
 * @param <T> 목록 요소 타입
 * @author CMS Team
 */
@Getter
public class PageResponse<T> {

    /**
     * 데이터 목록
     */
    private final List<T> content;

    /**
     * 현재 페이지 번호 (1부터 시작)
     */
    private final int page;

    /**
     * 페이지 크기
     */
    private final int size;

    /**
     * 전체 데이터 개수
     */
    private final long totalElements;

    /**
     * 전체 페이지 수
     */
    private final int totalPages;

    /**
     * 생성자
     * 
     * @param content 데이터 목록
     * @param page 현재 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @param totalElements 전체 데이터 개수
     */
    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    /**
     * 첫 페이지 여부
     * 
     * @return 첫 페이지이면 true
     */
    public boolean isFirst() {
        return page == 1;
    }

    /**
     * 마지막 페이지 여부
     * 
     * @return 마지막 페이지이면 true
     */
    public boolean isLast() {
        return page >= totalPages;
    }

    /**
     * 다음 페이지 존재 여부
     * 
     * @return 다음 페이지가 있으면 true
     */
    public boolean hasNext() {
        return page < totalPages;
    }

    /**
     * 이전 페이지 존재 여부
     * 
     * @return 이전 페이지가 있으면 true
     */
    public boolean hasPrevious() {
        return page > 1;
    }

    /**
     * PageResponse 생성 (정적 팩토리 메서드)
     * 
     * @param content 데이터 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 데이터 개수
     * @param <T> 요소 타입
     * @return 페이지 응답
     */
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        return new PageResponse<>(content, page, size, totalElements);
    }

    /**
     * 빈 페이지 응답 생성
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param <T> 요소 타입
     * @return 빈 페이지 응답
     */
    public static <T> PageResponse<T> empty(int page, int size) {
        return new PageResponse<>(List.of(), page, size, 0);
    }

    /**
     * offset 계산 (MyBatis 페이징용)
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @return offset 값
     */
    public static int calculateOffset(int page, int size) {
        return (page - 1) * size;
    }
}
