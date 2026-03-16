package com.nt.cms.install.service;

import com.nt.cms.common.config.CmsProperties;
import com.nt.cms.install.dto.AdminAccountRequest;
import com.nt.cms.install.dto.DatabaseConfigRequest;
import com.nt.cms.install.dto.SiteConfigRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DefaultInstallService 테스트
 * 
 * @author CMS Team
 */
@ExtendWith(MockitoExtension.class)
class DefaultInstallServiceTest {

    @Mock
    private CmsProperties cmsProperties;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DefaultInstallService installService;

    @BeforeEach
    void setUp() {
        installService = new DefaultInstallService(cmsProperties, passwordEncoder);
    }

    @Test
    @DisplayName("설치되지 않은 상태에서 isInstalled는 false를 반환해야 한다")
    void isInstalled_whenNotInstalled() {
        // given
        when(cmsProperties.isInstalled()).thenReturn(false);

        // when
        boolean result = installService.isInstalled();

        // then
        assertFalse(result);
        verify(cmsProperties).isInstalled();
    }

    @Test
    @DisplayName("설치된 상태에서 isInstalled는 true를 반환해야 한다")
    void isInstalled_whenInstalled() {
        // given
        when(cmsProperties.isInstalled()).thenReturn(true);

        // when
        boolean result = installService.isInstalled();

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("데이터베이스 설정 저장 및 조회가 정상 동작해야 한다")
    void saveDatabaseConfig() {
        // given
        DatabaseConfigRequest request = new DatabaseConfigRequest();
        request.setHost("localhost");
        request.setPort(3306);
        request.setDatabaseName("cms");
        request.setUsername("root");

        // when
        installService.saveDatabaseConfig(request);
        DatabaseConfigRequest saved = installService.getDatabaseConfig();

        // then
        assertNotNull(saved);
        assertEquals("localhost", saved.getHost());
        assertEquals(3306, saved.getPort());
        assertEquals("cms", saved.getDatabaseName());
        assertEquals("root", saved.getUsername());
    }

    @Test
    @DisplayName("설정 저장 전 getDatabaseConfig는 null을 반환해야 한다")
    void getDatabaseConfig_whenNotSaved() {
        // when
        DatabaseConfigRequest result = installService.getDatabaseConfig();

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("resetInstallation 후 설정이 초기화되어야 한다")
    void resetInstallation() {
        // given
        DatabaseConfigRequest dbConfig = new DatabaseConfigRequest();
        dbConfig.setHost("localhost");
        installService.saveDatabaseConfig(dbConfig);
        
        SiteConfigRequest siteConfig = new SiteConfigRequest();
        siteConfig.setSiteName("Test");
        installService.saveSiteConfig(siteConfig);

        // when
        installService.resetInstallation();

        // then
        assertNull(installService.getDatabaseConfig());
    }

    @Test
    @DisplayName("잘못된 DB 정보로 연결 테스트 시 false를 반환해야 한다")
    void testDatabaseConnection_withInvalidConfig() {
        // given
        DatabaseConfigRequest request = new DatabaseConfigRequest();
        request.setHost("invalid-host");
        request.setPort(3306);
        request.setDatabaseName("invalid_db");
        request.setUsername("invalid_user");
        request.setPassword("invalid_pass");

        // when
        boolean result = installService.testDatabaseConnection(request);

        // then
        assertFalse(result);
    }
}
