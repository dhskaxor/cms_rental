package com.nt.cms.publicapi.controller;

import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.auth.security.AdminSessionAuthenticationFilter;
import com.nt.cms.board.dto.BoardGroupResponse;
import com.nt.cms.board.dto.BoardResponse;
import com.nt.cms.board.dto.CommentResponse;
import com.nt.cms.board.dto.PostResponse;
import com.nt.cms.board.dto.PostSearchRequest;
import com.nt.cms.board.service.BoardGroupService;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.CommentService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.common.exception.GlobalExceptionHandler;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.role.mapper.RoleMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PublicBoardController 단위 테스트
 *
 * @author CMS Team
 */
@WebMvcTest(controllers = PublicBoardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PublicBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardGroupService boardGroupService;

    @MockBean
    private BoardService boardService;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AdminSessionAuthenticationFilter adminSessionAuthenticationFilter;

    @MockBean
    private RoleMapper roleMapper;

    @Nested
    @DisplayName("GET /api/v1/public/board-groups")
    class GetBoardGroups {

        @Test
        @DisplayName("게시판 그룹 목록 조회 성공")
        void getBoardGroups_success() throws Exception {
            when(boardGroupService.getGroups()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/public/board-groups"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards")
    class GetBoards {

        @Test
        @DisplayName("게시판 목록 조회 성공")
        void getBoards_success() throws Exception {
            var board = BoardResponse.builder().id(1L).boardCode("notice").boardName("공지사항").build();
            when(boardService.getBoards()).thenReturn(List.of(board));

            mockMvc.perform(get("/api/v1/public/boards"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].boardCode").value("notice"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/group/{groupId}")
    class GetBoardsByGroupId {

        @Test
        @DisplayName("그룹별 게시판 목록 조회 성공")
        void getBoardsByGroupId_success() throws Exception {
            when(boardService.getBoardsByGroupId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/public/boards/group/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/code/{boardCode}")
    class GetBoardByCode {

        @Test
        @DisplayName("게시판 코드로 조회 성공")
        void getBoardByCode_success() throws Exception {
            var board = BoardResponse.builder().id(1L).boardCode("notice").boardName("공지사항").build();
            when(boardService.getBoardByCode("notice")).thenReturn(board);

            mockMvc.perform(get("/api/v1/public/boards/code/notice"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.boardCode").value("notice"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/{boardId}/posts")
    class GetPosts {

        @Test
        @DisplayName("게시글 목록 조회 성공")
        void getPosts_success() throws Exception {
            var pageResponse = PageResponse.<PostResponse>empty(1, 10);
            when(postService.getPosts(eq(1L), any(PostSearchRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/v1/public/boards/1/posts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.page").value(1))
                    .andExpect(jsonPath("$.data.content").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/{boardId}/posts/notices")
    class GetNotices {

        @Test
        @DisplayName("공지글 목록 조회 성공")
        void getNotices_success() throws Exception {
            when(postService.getNotices(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/public/boards/1/posts/notices"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/{boardId}/posts/{postId}")
    class GetPost {

        @Test
        @DisplayName("게시글 상세 조회 성공")
        void getPost_success() throws Exception {
            var post = PostResponse.builder().id(1L).title("제목").content("내용").build();
            when(postService.getPost(1L, null, null)).thenReturn(post);

            mockMvc.perform(get("/api/v1/public/boards/1/posts/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("제목"));
        }

        @Test
        @DisplayName("비밀글 익명 조회 시 SECRET_POST_ACCESS_DENIED 예외 발생")
        void getPost_secretPost_anonymous_throwsException() throws Exception {
            // 비밀글 조회 시 BusinessException 발생을 검증
            when(postService.getPost(eq(1L), any(), any()))
                    .thenThrow(new com.nt.cms.common.exception.BusinessException(
                            com.nt.cms.common.exception.ErrorCode.SECRET_POST_ACCESS_DENIED));

            // 예외가 발생하여 ServletException으로 래핑되는지 확인
            try {
                mockMvc.perform(get("/api/v1/public/boards/1/posts/1"));
            } catch (jakarta.servlet.ServletException e) {
                org.junit.jupiter.api.Assertions.assertTrue(
                        e.getCause() instanceof com.nt.cms.common.exception.BusinessException);
                return;
            }
            org.junit.jupiter.api.Assertions.fail("BusinessException 예외가 발생해야 합니다.");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/{boardId}/posts/{postId}/prev")
    class GetPrevPost {

        @Test
        @DisplayName("이전 게시글 조회 성공")
        void getPrevPost_success() throws Exception {
            var post = PostResponse.builder().id(2L).title("이전글").build();
            when(postService.getPrevPost(1L, 3L)).thenReturn(post);

            mockMvc.perform(get("/api/v1/public/boards/1/posts/3/prev"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/{boardId}/posts/{postId}/next")
    class GetNextPost {

        @Test
        @DisplayName("다음 게시글 조회 성공")
        void getNextPost_success() throws Exception {
            var post = PostResponse.builder().id(4L).title("다음글").build();
            when(postService.getNextPost(1L, 3L)).thenReturn(post);

            mockMvc.perform(get("/api/v1/public/boards/1/posts/3/next"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(4));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/public/boards/{boardId}/posts/{postId}/comments")
    class GetComments {

        @Test
        @DisplayName("댓글 목록 조회 성공")
        void getComments_success() throws Exception {
            var comment = CommentResponse.builder().id(1L).postId(1L).content("댓글").build();
            when(commentService.getComments(1L)).thenReturn(List.of(comment));

            mockMvc.perform(get("/api/v1/public/boards/1/posts/1/comments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].content").value("댓글"));
        }
    }
}
