package com.nt.cms.site.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.auth.security.AdminSessionAuthenticationFilter;
import com.nt.cms.board.dto.BoardResponse;
import com.nt.cms.board.dto.PostResponse;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.file.service.FileService;
import com.nt.cms.menu.service.SiteMenuService;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import com.nt.cms.site.config.SiteModelAttributeAdvice;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SiteFormController 단위 테스트 (Page/Submit 방식)
 *
 * @author CMS Team
 */
@WebMvcTest(controllers = {SiteFormController.class})
@Import(SiteModelAttributeAdvice.class)
@AutoConfigureMockMvc(addFilters = false)
class SiteFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;
    @MockBean
    private PostService postService;
    @MockBean
    private FileService fileService;
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

    private BoardResponse boardWithCreate;
    private RoleVO roleAnonymous;

    @BeforeEach
    void setUp() {
        when(siteMenuService.findVisibleMenus(anyBoolean())).thenReturn(Collections.emptyList());
        roleAnonymous = RoleVO.builder().id(1L).roleCode("ANONYMOUS").build();
        when(roleMapper.findByRoleCode("ANONYMOUS")).thenReturn(roleAnonymous);

        var perm = BoardResponse.BoardPermissionResponse.builder()
                .roleCode("ANONYMOUS")
                .canCreate(true)
                .canRead(true)
                .canUpdate(true)
                .canDelete(true)
                .build();
        boardWithCreate = BoardResponse.builder()
                .id(1L)
                .boardCode("notice")
                .boardName("공지사항")
                .useFile(false)
                .permissions(List.of(perm))
                .build();
    }

    @Nested
    @DisplayName("POST /site/board/{boardPath}/post - 게시글 작성")
    class CreatePost {

        @Test
        @DisplayName("게시판 없음 - 메인으로 리다이렉트")
        void createPost_boardNotFound() throws Exception {
            when(boardService.getBoardByCode("unknown")).thenReturn(null);
            when(boardService.getBoard(1L)).thenReturn(null);

            mockMvc.perform(post("/site/board/unknown/post")
                            .with(csrf())
                            .param("title", "제목")
                            .param("content", "내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/"));
        }

        @Test
        @DisplayName("권한 없음 - 게시판 목록으로 리다이렉트")
        void createPost_noPermission() throws Exception {
            var perm = BoardResponse.BoardPermissionResponse.builder()
                    .roleCode("ANONYMOUS")
                    .canCreate(false)
                    .canRead(true)
                    .build();
            var board = BoardResponse.builder()
                    .id(1L)
                    .boardCode("notice")
                    .permissions(List.of(perm))
                    .build();
            when(boardService.getBoardByCode("notice")).thenReturn(board);

            mockMvc.perform(post("/site/board/notice/post")
                            .with(csrf())
                            .param("title", "제목")
                            .param("content", "내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/board/notice"));
        }

        @Test
        @DisplayName("게시글 작성 성공 - 상세로 리다이렉트")
        void createPost_success() throws Exception {
            when(boardService.getBoardByCode("notice")).thenReturn(boardWithCreate);
            PostResponse created = PostResponse.builder().id(10L).build();
            when(postService.createPost(eq(1L), any(), isNull(), eq(1L))).thenReturn(created);

            mockMvc.perform(post("/site/board/notice/post")
                            .with(csrf())
                            .param("title", "제목")
                            .param("content", "내용"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/board/notice/post/10"));
        }
    }

    @Nested
    @DisplayName("POST /site/board/{boardPath}/post/{postId}/delete - 게시글 삭제")
    class DeletePost {

        @Test
        @DisplayName("게시글 없음 - 목록으로 리다이렉트")
        void deletePost_postNotFound() throws Exception {
            when(boardService.getBoardByCode("notice")).thenReturn(boardWithCreate);
            when(postService.getPost(100L, null, 1L)).thenReturn(null);

            mockMvc.perform(post("/site/board/notice/post/100/delete")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/board/notice"));
        }

        @Test
        @DisplayName("삭제 권한 없음 - 상세로 리다이렉트")
        void deletePost_noPermission() throws Exception {
            var perm = BoardResponse.BoardPermissionResponse.builder()
                    .roleCode("ANONYMOUS")
                    .canRead(true)
                    .canDelete(false)
                    .build();
            var board = BoardResponse.builder()
                    .id(1L)
                    .boardCode("notice")
                    .permissions(List.of(perm))
                    .build();
            when(boardService.getBoardByCode("notice")).thenReturn(board);
            PostResponse post = PostResponse.builder().id(100L).writerId(999L).build();
            when(postService.getPost(100L, null, 1L)).thenReturn(post);

            mockMvc.perform(post("/site/board/notice/post/100/delete")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/board/notice/post/100"));
        }

        @Test
        @DisplayName("삭제 성공 - 목록으로 리다이렉트")
        void deletePost_success() throws Exception {
            var permMember = BoardResponse.BoardPermissionResponse.builder()
                    .roleCode("MEMBER")
                    .canRead(true)
                    .canDelete(true)
                    .build();
            var boardForDelete = BoardResponse.builder()
                    .id(1L)
                    .boardCode("notice")
                    .permissions(List.of(permMember))
                    .build();
            when(boardService.getBoardByCode("notice")).thenReturn(boardForDelete);
            SessionUser sessionUser = SessionUser.builder().id(5L).roleCode("MEMBER").build();
            when(roleMapper.findByRoleCode("MEMBER")).thenReturn(RoleVO.builder().id(2L).roleCode("MEMBER").build());
            PostResponse post = PostResponse.builder().id(100L).writerId(5L).build();
            when(postService.getPost(100L, 5L, 2L)).thenReturn(post);

            mockMvc.perform(post("/site/board/notice/post/100/delete")
                            .with(csrf())
                            .sessionAttr(SessionConstants.SITE_CURRENT_USER, sessionUser))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/board/notice"));
        }
    }
}
