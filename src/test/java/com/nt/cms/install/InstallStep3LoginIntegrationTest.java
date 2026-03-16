package com.nt.cms.install;

import com.nt.cms.install.dto.AdminAccountRequest;
import com.nt.cms.install.dto.DatabaseConfigRequest;
import com.nt.cms.install.service.InstallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Step3 관리자 생성 후 로그인 통합 테스트
 * 
 * <p>설치 Step3에서 생성한 관리자 계정으로 웹 로그인이 성공하는지 검증한다.</p>
 *
 * @author CMS Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "cms.installed=false")
class InstallStep3LoginIntegrationTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin1!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InstallService installService;

    @BeforeEach
    void setUp() {
        // 테스트용 H2 URL (application-test.yml과 동일한 DB 사용)
        String h2Url = "jdbc:h2:mem:cmstest;MODE=MariaDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

        DatabaseConfigRequest dbConfig = new DatabaseConfigRequest();
        dbConfig.setUsername("sa");
        dbConfig.setPassword("");
        dbConfig.setJdbcUrlOverride(h2Url);
        installService.saveDatabaseConfig(dbConfig);

        // Step2: 스키마 + 초기 데이터 (H2용)
        installService.executeSchema("schema/schema-h2.sql");
        installService.insertInitialData();

        // Step3: 관리자 계정 생성
        AdminAccountRequest adminRequest = new AdminAccountRequest();
        adminRequest.setUsername(ADMIN_USERNAME);
        adminRequest.setPassword(ADMIN_PASSWORD);
        adminRequest.setPasswordConfirm(ADMIN_PASSWORD);
        adminRequest.setName("관리자");
        adminRequest.setEmail("admin@example.com");
        installService.createAdminAccount(adminRequest);
    }

    @Test
    @DisplayName("Step3에서 생성한 관리자 계정으로 웹 로그인에 성공해야 한다")
    void step3Admin_canLoginSuccessfully() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .param("username", ADMIN_USERNAME)
                        .param("password", ADMIN_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 실패해야 한다")
    void step3Admin_wrongPassword_loginFails() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .param("username", ADMIN_USERNAME)
                        .param("password", "WrongPass1!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}
