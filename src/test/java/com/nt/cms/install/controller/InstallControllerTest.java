package com.nt.cms.install.controller;

import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.install.dto.DatabaseConfigRequest;
import com.nt.cms.install.service.InstallService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * InstallController 테스트
 * 
 * @author CMS Team
 */
@WebMvcTest(controllers = InstallController.class)
@AutoConfigureMockMvc(addFilters = false)
class InstallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstallService installService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("설치 시작 페이지에 접근할 수 있어야 한다")
    void index() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);

        // when & then
        mockMvc.perform(get("/install"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/step1-database"))
                .andExpect(model().attributeExists("databaseConfig"))
                .andExpect(model().attribute("currentStep", 1));
    }

    @Test
    @DisplayName("이미 설치된 경우 already-installed 페이지를 반환해야 한다")
    void index_whenAlreadyInstalled() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(true);

        // when & then
        mockMvc.perform(get("/install"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/already-installed"));
    }

    @Test
    @DisplayName("Step2 페이지는 DB 설정이 있어야 접근 가능해야 한다")
    void step2_withoutConfig() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);
        when(installService.getDatabaseConfig()).thenReturn(null);

        // when & then
        mockMvc.perform(get("/install/step2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/install"));
    }

    @Test
    @DisplayName("Step2 페이지는 DB 설정이 있으면 정상 접근해야 한다")
    void step2_withConfig() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);
        when(installService.getDatabaseConfig()).thenReturn(new DatabaseConfigRequest());

        // when & then
        mockMvc.perform(get("/install/step2"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/step2-schema"))
                .andExpect(model().attribute("currentStep", 2));
    }

    @Test
    @DisplayName("Step3 페이지는 DB 설정이 있어야 접근 가능해야 한다")
    void step3_withConfig() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);
        when(installService.getDatabaseConfig()).thenReturn(new DatabaseConfigRequest());

        // when & then
        mockMvc.perform(get("/install/step3"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/step3-admin"))
                .andExpect(model().attribute("currentStep", 3));
    }

    @Test
    @DisplayName("Step4 페이지는 DB 설정이 있어야 접근 가능해야 한다")
    void step4_withConfig() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);
        when(installService.getDatabaseConfig()).thenReturn(new DatabaseConfigRequest());

        // when & then
        mockMvc.perform(get("/install/step4"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/step4-config"))
                .andExpect(model().attribute("currentStep", 4));
    }

    @Test
    @DisplayName("Step5 페이지는 DB 설정이 있어야 접근 가능해야 한다")
    void step5_withConfig() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);
        when(installService.getDatabaseConfig()).thenReturn(new DatabaseConfigRequest());

        // when & then
        mockMvc.perform(get("/install/step5"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/step5-complete"))
                .andExpect(model().attribute("currentStep", 5));
    }

    @Test
    @DisplayName("이미 설치됨 페이지에 접근할 수 있어야 한다")
    void alreadyInstalled() throws Exception {
        // when & then
        mockMvc.perform(get("/install/already-installed"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/already-installed"));
    }

    @Test
    @DisplayName("설치 성공 페이지에 접근할 수 있어야 한다")
    void success() throws Exception {
        // when & then
        mockMvc.perform(get("/install/success"))
                .andExpect(status().isOk())
                .andExpect(view().name("install/success"));
    }

    @Test
    @WithMockUser
    @DisplayName("Step2 처리 성공 시 Step3로 리다이렉트해야 한다")
    void processStep2_success() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);
        when(installService.executeSchema()).thenReturn(true);
        when(installService.insertInitialData()).thenReturn(true);

        // when & then
        mockMvc.perform(post("/install/step2").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/install/step3"));
    }

    @Test
    @WithMockUser
    @DisplayName("Step5 처리 성공 시 success로 리다이렉트해야 한다")
    void processStep5_success() throws Exception {
        // given
        when(installService.isInstalled()).thenReturn(false);
        when(installService.completeInstallation()).thenReturn(true);

        // when & then
        mockMvc.perform(post("/install/step5").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/install/success"));
    }
}
