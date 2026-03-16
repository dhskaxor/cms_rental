package com.nt.cms.publicapi.controller;

import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.auth.security.AdminSessionAuthenticationFilter;
import com.nt.cms.common.exception.GlobalExceptionHandler;
import com.nt.cms.commoncode.dto.CommonCodeResponse;
import com.nt.cms.commoncode.service.CommonCodeService;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.service.SiteMenuService;
import com.nt.cms.menu.service.SitePageService;
import com.nt.cms.popup.service.PopupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PublicSiteController 단위 테스트
 *
 * @author CMS Team
 */
@WebMvcTest(controllers = PublicSiteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PublicSiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SiteMenuService siteMenuService;

    @MockBean
    private SitePageService sitePageService;

    @MockBean
    private PopupService popupService;

    @MockBean
    private CommonCodeService commonCodeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AdminSessionAuthenticationFilter adminSessionAuthenticationFilter;

    @Nested
    @DisplayName("GET /api/v1/public/menus")
    class GetMenus {

        @Test
        @DisplayName("메뉴 목록 조회 성공")
        void getMenus_success() throws Exception {
            when(siteMenuService.findVisibleMenus(anyBoolean())).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/public/menus"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @DisplayName("includeLoginRequired 파라미터 전달 시 서비스에 전달")
        void getMenus_withIncludeLoginRequired() throws Exception {
            var menu = SiteMenuResponse.builder().id(1L).menuName("메뉴1").depth(1).build();
            when(siteMenuService.findVisibleMenus(true)).thenReturn(List.of(menu));

            mockMvc.perform(get("/api/v1/public/menus").param("includeLoginRequired", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].menuName").value("메뉴1"));
        }

        @Test
        @DisplayName("게시판 메뉴 시 boardCode 응답 포함")
        void getMenus_boardMenuIncludesBoardCode() throws Exception {
            var menu = SiteMenuResponse.builder()
                    .id(1L)
                    .menuName("공지사항")
                    .menuType("BOARD")
                    .boardId(1L)
                    .boardCode("notice")
                    .boardName("공지사항")
                    .depth(1)
                    .build();
            when(siteMenuService.findVisibleMenus(anyBoolean())).thenReturn(List.of(menu));

            mockMvc.perform(get("/api/v1/public/menus"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].boardCode").value("notice"))
                    .andExpect(jsonPath("$.data[0].boardId").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/pages/{pageCode}")
    class GetPage {

        @Test
        @DisplayName("페이지 조회 성공")
        void getPage_success() throws Exception {
            var response = SitePageResponse.builder()
                    .id(1L).pageCode("about").pageTitle("회사소개").content("내용").isPublished(true).build();
            when(sitePageService.findByPageCode("about")).thenReturn(response);

            mockMvc.perform(get("/api/v1/public/pages/about"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.pageCode").value("about"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/popups")
    class GetPopups {

        @Test
        @DisplayName("팝업 목록 조회 성공")
        void getPopups_success() throws Exception {
            when(popupService.getDisplayPopups(anyString(), anyString(), anyBoolean()))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/public/popups"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/common-codes/{groupCode}")
    class GetCommonCodes {

        @Test
        @DisplayName("공통코드 조회 성공")
        void getCommonCodes_success() throws Exception {
            var code = CommonCodeResponse.builder().id(1L).groupId(1L).code("NOTICE").codeName("공지").build();
            when(commonCodeService.getActiveCodesByGroupCode("BOARD_TYPE")).thenReturn(List.of(code));

            mockMvc.perform(get("/api/v1/public/common-codes/BOARD_TYPE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data[0].code").value("NOTICE"));
        }
    }
}
