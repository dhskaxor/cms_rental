package com.nt.cms.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserSearchRequest 테스트
 */
@DisplayName("UserSearchRequest 테스트")
class UserSearchRequestTest {

    @Test
    @DisplayName("기본값 확인")
    void testDefaultValues() {
        // given & when
        UserSearchRequest request = UserSearchRequest.builder().build();

        // then
        assertThat(request.getPage()).isEqualTo(1);
        assertThat(request.getSize()).isEqualTo(10);
        assertThat(request.getKeyword()).isNull();
        assertThat(request.getRoleId()).isNull();
        assertThat(request.getStatus()).isNull();
    }

    @Test
    @DisplayName("offset 계산 - 1페이지")
    void testGetOffset_page1() {
        // given
        UserSearchRequest request = UserSearchRequest.builder()
                .page(1)
                .size(10)
                .build();

        // when
        int offset = request.getOffset();

        // then
        assertThat(offset).isEqualTo(0);
    }

    @Test
    @DisplayName("offset 계산 - 2페이지")
    void testGetOffset_page2() {
        // given
        UserSearchRequest request = UserSearchRequest.builder()
                .page(2)
                .size(10)
                .build();

        // when
        int offset = request.getOffset();

        // then
        assertThat(offset).isEqualTo(10);
    }

    @Test
    @DisplayName("offset 계산 - 3페이지, 크기 20")
    void testGetOffset_page3_size20() {
        // given
        UserSearchRequest request = UserSearchRequest.builder()
                .page(3)
                .size(20)
                .build();

        // when
        int offset = request.getOffset();

        // then
        assertThat(offset).isEqualTo(40);
    }

    @Test
    @DisplayName("검색 조건 설정")
    void testSearchConditions() {
        // given & when
        UserSearchRequest request = UserSearchRequest.builder()
                .keyword("test")
                .roleId(1L)
                .status("ACTIVE")
                .page(1)
                .size(10)
                .build();

        // then
        assertThat(request.getKeyword()).isEqualTo("test");
        assertThat(request.getRoleId()).isEqualTo(1L);
        assertThat(request.getStatus()).isEqualTo("ACTIVE");
    }
}
