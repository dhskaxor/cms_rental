package com.nt.cms.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResponse 단위 테스트
 */
class PageResponseTest {

    @Test
    @DisplayName("페이지 응답 생성")
    void create_pageResponse() {
        // given
        List<String> content = Arrays.asList("item1", "item2", "item3");
        int page = 1;
        int size = 10;
        long totalElements = 25;
        
        // when
        PageResponse<String> response = new PageResponse<>(content, page, size, totalElements);
        
        // then
        assertEquals(content, response.getContent());
        assertEquals(page, response.getPage());
        assertEquals(size, response.getSize());
        assertEquals(totalElements, response.getTotalElements());
        assertEquals(3, response.getTotalPages()); // 25 / 10 = 3 (올림)
    }

    @Test
    @DisplayName("첫 페이지 확인")
    void isFirst() {
        // given
        PageResponse<String> firstPage = new PageResponse<>(List.of(), 1, 10, 100);
        PageResponse<String> secondPage = new PageResponse<>(List.of(), 2, 10, 100);
        
        // then
        assertTrue(firstPage.isFirst());
        assertFalse(secondPage.isFirst());
    }

    @Test
    @DisplayName("마지막 페이지 확인")
    void isLast() {
        // given
        PageResponse<String> lastPage = new PageResponse<>(List.of(), 10, 10, 100);
        PageResponse<String> firstPage = new PageResponse<>(List.of(), 1, 10, 100);
        
        // then
        assertTrue(lastPage.isLast());
        assertFalse(firstPage.isLast());
    }

    @Test
    @DisplayName("다음/이전 페이지 존재 확인")
    void hasNextAndPrevious() {
        // given
        PageResponse<String> firstPage = new PageResponse<>(List.of(), 1, 10, 100);
        PageResponse<String> middlePage = new PageResponse<>(List.of(), 5, 10, 100);
        PageResponse<String> lastPage = new PageResponse<>(List.of(), 10, 10, 100);
        
        // then
        assertTrue(firstPage.hasNext());
        assertFalse(firstPage.hasPrevious());
        
        assertTrue(middlePage.hasNext());
        assertTrue(middlePage.hasPrevious());
        
        assertFalse(lastPage.hasNext());
        assertTrue(lastPage.hasPrevious());
    }

    @Test
    @DisplayName("빈 페이지 생성")
    void empty_pageResponse() {
        // when
        PageResponse<String> response = PageResponse.empty(1, 10);
        
        // then
        assertTrue(response.getContent().isEmpty());
        assertEquals(1, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
    }

    @Test
    @DisplayName("offset 계산")
    void calculateOffset() {
        // then
        assertEquals(0, PageResponse.calculateOffset(1, 10));
        assertEquals(10, PageResponse.calculateOffset(2, 10));
        assertEquals(90, PageResponse.calculateOffset(10, 10));
        assertEquals(0, PageResponse.calculateOffset(1, 20));
        assertEquals(20, PageResponse.calculateOffset(2, 20));
    }
}
