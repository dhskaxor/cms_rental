package com.nt.cms.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.mapper.BoardMapper;
import com.nt.cms.board.mapper.PostMapper;
import com.nt.cms.board.vo.BoardPermissionVO;
import com.nt.cms.board.vo.BoardVO;
import com.nt.cms.board.vo.PostVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.file.service.FileService;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import com.nt.cms.user.vo.UserVO;
import com.nt.cms.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * DefaultPostService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultPostService 테스트")
class DefaultPostServiceTest {

    @Mock
    private PostMapper postMapper;

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private FileService fileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DefaultPostService postService;

    private BoardVO testBoard;
    private PostVO testPost;

    @BeforeEach
    void setUp() {
        postService = new DefaultPostService(postMapper, boardMapper, fileService, userMapper, roleMapper, auditLogService, objectMapper);
        testBoard = BoardVO.builder()
                .id(1L)
                .boardCode("free")
                .boardName("자유게시판")
                .useComment(true)
                .useSecret(false)
                .build();

        testPost = PostVO.builder()
                .id(1L)
                .boardId(1L)
                .boardCode("free")
                .boardName("자유게시판")
                .title("테스트 게시글")
                .content("테스트 내용")
                .writerId(1L)
                .writerName("테스트 작성자")
                .viewCount(10)
                .isNotice(false)
                .isSecret(false)
                .commentCount(5)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("게시글 목록 조회")
    class GetPostsTest {

        @Test
        @DisplayName("게시글 목록 조회 성공")
        void getPosts_success() {
            // given
            PostSearchRequest request = PostSearchRequest.builder()
                    .page(1)
                    .size(10)
                    .build();

            given(boardMapper.findById(1L)).willReturn(testBoard);
            given(postMapper.findByBoardId(eq(1L), any())).willReturn(Arrays.asList(testPost));
            given(postMapper.countByBoardId(eq(1L), any())).willReturn(1L);

            // when
            PageResponse<PostResponse> response = postService.getPosts(1L, request);

            // then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 게시판일 때 예외 발생")
        void getPosts_boardNotFound() {
            // given
            PostSearchRequest request = PostSearchRequest.builder().build();
            given(boardMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> postService.getPosts(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시글 상세 조회")
    class GetPostTest {

        @Test
        @DisplayName("게시글 조회 성공")
        void getPost_success() {
            // given
            given(postMapper.findById(1L)).willReturn(testPost);

            // when
            PostResponse response = postService.getPost(1L, 1L, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("테스트 게시글");
            verify(postMapper).increaseViewCount(1L);
        }

        @Test
        @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
        void getPost_notFound() {
            // given
            given(postMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> postService.getPost(999L, 1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("비밀글 - 작성자가 아닌 경우 예외 발생")
        void getPost_secretAccessDenied() {
            // given
            PostVO secretPost = PostVO.builder()
                    .id(2L)
                    .boardId(1L)
                    .title("비밀글")
                    .writerId(2L)
                    .isSecret(true)
                    .build();

            given(postMapper.findById(2L)).willReturn(secretPost);
            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 3L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> postService.getPost(2L, 1L, 3L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SECRET_POST_ACCESS_DENIED);
        }

        @Test
        @DisplayName("비밀글 - 익명 사용자(null, null) 조회 시 SECRET_POST_ACCESS_DENIED")
        void getPost_secretPostAnonymousUser_denied() {
            // given: PublicBoardController에서 getPost(postId, null, null) 호출 시나리오
            PostVO secretPost = PostVO.builder()
                    .id(2L)
                    .boardId(1L)
                    .title("비밀글")
                    .writerId(1L)
                    .isSecret(true)
                    .build();

            given(postMapper.findById(2L)).willReturn(secretPost);

            // when & then: userId=null, roleId=null 이면 비밀글 접근 차단
            assertThatThrownBy(() -> postService.getPost(2L, null, null))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SECRET_POST_ACCESS_DENIED);
        }

        @Test
        @DisplayName("일반글 - 익명 사용자 조회 허용")
        void getPost_normalPostAnonymousUser_success() {
            // given: 일반글은 roleId=null, userId=null로도 조회 가능
            given(postMapper.findById(1L)).willReturn(testPost);

            // when
            PostResponse response = postService.getPost(1L, null, null);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("테스트 게시글");
            verify(postMapper).increaseViewCount(1L);
        }
    }

    @Nested
    @DisplayName("게시글 생성")
    class CreatePostTest {

        @Test
        @DisplayName("게시글 생성 성공")
        void createPost_success() {
            // given
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("새 게시글")
                    .content("새 내용")
                    .isNotice(false)
                    .isSecret(false)
                    .build();

            BoardPermissionVO permission = BoardPermissionVO.builder()
                    .canCreate(true)
                    .build();

            given(boardMapper.findById(1L)).willReturn(testBoard);
            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 1L)).willReturn(permission);
            given(userMapper.findUsernameById(1L)).willReturn("admin");
            given(postMapper.insert(any(PostVO.class))).willAnswer(invocation -> {
                PostVO post = invocation.getArgument(0);
                post.setId(2L);
                return 1;
            });
            given(postMapper.findById(2L)).willReturn(PostVO.builder()
                    .id(2L)
                    .boardId(1L)
                    .title("새 게시글")
                    .content("새 내용")
                    .build());

            // when
            PostResponse response = postService.createPost(1L, request, 1L, 1L);

            // then
            assertThat(response).isNotNull();
            verify(postMapper).insert(any(PostVO.class));
        }

        @Test
        @DisplayName("비회원(익명) 게시글 생성 - ANONYMOUS 권한 있으면 성공")
        void createPost_anonymousUser_success() {
            // given: roleId=null, userId=null → ANONYMOUS 역할, anonymous 시스템 계정 사용
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("익명 게시글")
                    .content("익명 내용")
                    .isNotice(false)
                    .isSecret(false)
                    .build();

            RoleVO anonymousRole = RoleVO.builder().id(4L).roleCode("ANONYMOUS").roleName("비회원").build();
            UserVO anonymousUser = UserVO.builder().id(99L).username("anonymous").name("비회원").build();
            BoardPermissionVO permission = BoardPermissionVO.builder().canCreate(true).build();

            given(boardMapper.findById(1L)).willReturn(testBoard);
            given(roleMapper.findByRoleCode("ANONYMOUS")).willReturn(anonymousRole);
            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 4L)).willReturn(permission);
            given(userMapper.findByUsername("anonymous")).willReturn(anonymousUser);
            given(userMapper.findUsernameById(99L)).willReturn("anonymous");
            given(postMapper.insert(any(PostVO.class))).willAnswer(invocation -> {
                PostVO post = invocation.getArgument(0);
                post.setId(100L);
                return 1;
            });
            given(postMapper.findById(100L)).willReturn(PostVO.builder()
                    .id(100L)
                    .boardId(1L)
                    .title("익명 게시글")
                    .content("익명 내용")
                    .writerId(99L)
                    .build());

            // when
            PostResponse response = postService.createPost(1L, request, null, null);

            // then
            assertThat(response).isNotNull();
            verify(postMapper).insert(argThat((PostVO post) -> Long.valueOf(99L).equals(post.getWriterId())));
        }

        @Test
        @DisplayName("권한이 없을 때 예외 발생")
        void createPost_permissionDenied() {
            // given
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("새 게시글")
                    .content("새 내용")
                    .build();

            given(boardMapper.findById(1L)).willReturn(testBoard);
            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 3L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> postService.createPost(1L, request, 1L, 3L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_PERMISSION_DENIED);
        }
    }

    @Nested
    @DisplayName("게시글 삭제")
    class DeletePostTest {

        @Test
        @DisplayName("작성자가 삭제 성공")
        void deletePost_byWriter() {
            // given
            given(postMapper.findById(1L)).willReturn(testPost);
            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 1L)).willReturn(null);
            given(userMapper.findUsernameById(1L)).willReturn("admin");

            // when
            postService.deletePost(1L, 1L, 1L);

            // then
            verify(postMapper).delete(1L, 1L);
        }

        @Test
        @DisplayName("권한 없는 사용자 삭제 시 예외 발생")
        void deletePost_accessDenied() {
            // given
            given(postMapper.findById(1L)).willReturn(testPost);
            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 3L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> postService.deletePost(1L, 999L, 3L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_ACCESS_DENIED);
        }
    }
}
