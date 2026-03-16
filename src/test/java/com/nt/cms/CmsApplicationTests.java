package com.nt.cms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * CMS 애플리케이션 컨텍스트 로드 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
class CmsApplicationTests {

    /**
     * 애플리케이션 컨텍스트가 정상적으로 로드되는지 확인
     */
    @Test
    @DisplayName("애플리케이션 컨텍스트 로드 테스트")
    void contextLoads() {
        // 컨텍스트가 정상 로드되면 테스트 통과
    }
}
