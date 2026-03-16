package com.nt.cms.auth.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RefreshTokenVO 테스트
 * 
 * @author CMS Team
 */
class RefreshTokenVOTest {

    @Nested
    @DisplayName("토큰 만료 테스트")
    class ExpirationTest {

        @Test
        @DisplayName("만료되지 않은 토큰은 isExpired()가 false를 반환해야 한다")
        void isExpired_notExpired() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(false)
                    .deleted(false)
                    .build();

            // then
            assertFalse(token.isExpired());
        }

        @Test
        @DisplayName("만료된 토큰은 isExpired()가 true를 반환해야 한다")
        void isExpired_expired() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(LocalDateTime.now().minusDays(1))
                    .revoked(false)
                    .deleted(false)
                    .build();

            // then
            assertTrue(token.isExpired());
        }

        @Test
        @DisplayName("만료 시간이 null이면 isExpired()가 false를 반환해야 한다")
        void isExpired_nullExpiration() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(null)
                    .revoked(false)
                    .deleted(false)
                    .build();

            // then
            assertFalse(token.isExpired());
        }
    }

    @Nested
    @DisplayName("토큰 유효성 테스트")
    class ValidityTest {

        @Test
        @DisplayName("유효한 토큰은 isValid()가 true를 반환해야 한다")
        void isValid_valid() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(false)
                    .deleted(false)
                    .build();

            // then
            assertTrue(token.isValid());
        }

        @Test
        @DisplayName("폐기된 토큰은 isValid()가 false를 반환해야 한다")
        void isValid_revoked() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(true)
                    .deleted(false)
                    .build();

            // then
            assertFalse(token.isValid());
        }

        @Test
        @DisplayName("만료된 토큰은 isValid()가 false를 반환해야 한다")
        void isValid_expired() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(LocalDateTime.now().minusDays(1))
                    .revoked(false)
                    .deleted(false)
                    .build();

            // then
            assertFalse(token.isValid());
        }

        @Test
        @DisplayName("삭제된 토큰은 isValid()가 false를 반환해야 한다")
        void isValid_deleted() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(false)
                    .deleted(true)
                    .build();

            // then
            assertFalse(token.isValid());
        }

        @Test
        @DisplayName("revoked가 null이면 isValid()가 true를 반환해야 한다")
        void isValid_revokedNull() {
            // given
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("token")
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(null)
                    .deleted(false)
                    .build();

            // then
            assertTrue(token.isValid());
        }
    }

    @Nested
    @DisplayName("Builder 테스트")
    class BuilderTest {

        @Test
        @DisplayName("모든 필드가 올바르게 설정되어야 한다")
        void builder_allFields() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusDays(14);

            // when
            RefreshTokenVO token = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(100L)
                    .refreshToken("test-refresh-token")
                    .expiresAt(expiresAt)
                    .revoked(false)
                    .userAgent("Mozilla/5.0")
                    .ipAddress("192.168.1.1")
                    .createdAt(now)
                    .deleted(false)
                    .build();

            // then
            assertEquals(1L, token.getId());
            assertEquals(100L, token.getUserId());
            assertEquals("test-refresh-token", token.getRefreshToken());
            assertEquals(expiresAt, token.getExpiresAt());
            assertFalse(token.getRevoked());
            assertEquals("Mozilla/5.0", token.getUserAgent());
            assertEquals("192.168.1.1", token.getIpAddress());
            assertEquals(now, token.getCreatedAt());
            assertFalse(token.getDeleted());
        }
    }
}
