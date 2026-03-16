package com.nt.cms.site.controller;

import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.auth.security.AdminSessionAuthenticationFilter;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.CommentService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.file.service.FileService;
import com.nt.cms.menu.service.SiteMenuService;
import com.nt.cms.menu.service.SitePageService;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.site.config.SiteModelAttributeAdvice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SiteViewController 단위 테스트 (Page/Submit 방식)
 *
 * @author CMS Team
 */
@WebMvcTest(controllers = {SiteViewController.class})
@Import(SiteModelAttributeAdvice.class)
@AutoConfigureMockMvc(addFilters = false)
class SiteControllerTest {

    @MockBean
    private BoardService boardService;
    @MockBean
    private PostService postService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private FileService fileService;
    @MockBean
    private SitePageService sitePageService;
    @MockBean
    private RoleMapper roleMapper;
    @MockBean
    private SiteMenuService siteMenuService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private AdminSessionAuthenticationFilter adminSessionAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        when(siteMenuService.findVisibleMenus(anyBoolean())).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("GET /site - site/index Thymeleaf 뷰 반환")
    void index_slash() throws Exception {
        mockMvc.perform(get("/site"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/index"));
    }

    @Test
    @DisplayName("GET /site/ - site/index Thymeleaf 뷰 반환")
    void index_trailingSlash() throws Exception {
        mockMvc.perform(get("/site/"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/index"));
    }

    @Test
    @DisplayName("GET /site/help - site/help 뷰 반환")
    void help() throws Exception {
        mockMvc.perform(get("/site/help"))
                .andExpect(status().isOk())
                .andExpect(view().name("site/help"));
    }

    @Test
    @DisplayName("GET /site/index.html - /site/로 리다이렉트")
    void indexHtml_redirects() throws Exception {
        mockMvc.perform(get("/site/index.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/site/"));
    }
}
