package com.nt.cms.board.service;

import com.nt.cms.board.dto.*;
import com.nt.cms.board.mapper.BoardMapper;
import com.nt.cms.board.mapper.CommentMapper;
import com.nt.cms.board.mapper.PostMapper;
import com.nt.cms.board.vo.BoardVO;
import com.nt.cms.board.vo.CommentVO;
import com.nt.cms.board.vo.PostVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * DefaultCommentService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultCommentService 테스트")
class DefaultCommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostMapper postMapper;

    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private DefaultCommentService commentService;

    private PostVO testPost;
    private BoardVO testBoard;
    private CommentVO testComment;

    @BeforeEach
    void setUp() {
        testBoard = BoardVO.builder()
                .id(1L)
                .boardCode("free")
                .useComment(true)
                .build();

        testPost = PostVO.builder()
                .id(1L)
                .boardId(1L)
                .title("테스트 게시글")
                .build();

        testComment = CommentVO.builder()
                .id(1L)
                .postId(1L)
                .parentId(null)
                .content("테스트 댓글")
                .writerId(1L)
                .writerName("테스트 작성자")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();
    }

    @Nested
    @DisplayName("댓글 목록 조회")
    class GetCommentsTest {

        @Test
        @DisplayName("댓글 목록 조회 성공")
        void getComments_success() {
            // given
            given(commentMapper.findByPostId(1L)).willReturn(Arrays.asList(testComment));
            given(commentMapper.findRepliesByParentId(1L)).willReturn(Collections.emptyList());

            // when
            List<CommentResponse> response = commentService.getComments(1L);

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getContent()).isEqualTo("테스트 댓글");
        }
    }

    @Nested
    @DisplayName("댓글 생성")
    class CreateCommentTest {

        @Test
        @DisplayName("댓글 생성 성공")
        void createComment_success() {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("새 댓글")
                    .build();

            given(postMapper.findById(1L)).willReturn(testPost);
            given(boardMapper.findById(1L)).willReturn(testBoard);
            given(commentMapper.insert(any(CommentVO.class))).willAnswer(invocation -> {
                CommentVO comment = invocation.getArgument(0);
                comment.setId(2L);
                return 1;
            });
            given(commentMapper.findById(2L)).willReturn(CommentVO.builder()
                    .id(2L)
                    .postId(1L)
                    .content("새 댓글")
                    .writerId(1L)
                    .build());

            // when
            CommentResponse response = commentService.createComment(1L, request, 1L);

            // then
            assertThat(response).isNotNull();
            verify(commentMapper).insert(any(CommentVO.class));
        }

        @Test
        @DisplayName("댓글 비활성화 게시판에서 댓글 생성 시 예외 발생")
        void createComment_disabled() {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("새 댓글")
                    .build();

            BoardVO disabledBoard = BoardVO.builder()
                    .id(1L)
                    .useComment(false)
                    .build();

            given(postMapper.findById(1L)).willReturn(testPost);
            given(boardMapper.findById(1L)).willReturn(disabledBoard);

            // when & then
            assertThatThrownBy(() -> commentService.createComment(1L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_DISABLED);
        }

        @Test
        @DisplayName("대댓글의 대댓글 생성 시 예외 발생")
        void createComment_depthExceeded() {
            // given
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .parentId(2L)
                    .content("대대댓글")
                    .build();

            CommentVO parentReply = CommentVO.builder()
                    .id(2L)
                    .postId(1L)
                    .parentId(1L)
                    .deleted(false)
                    .build();

            given(postMapper.findById(1L)).willReturn(testPost);
            given(boardMapper.findById(1L)).willReturn(testBoard);
            given(commentMapper.findById(2L)).willReturn(parentReply);

            // when & then
            assertThatThrownBy(() -> commentService.createComment(1L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMENT_DEPTH_EXCEEDED);
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteCommentTest {

        @Test
        @DisplayName("작성자가 삭제 성공")
        void deleteComment_byWriter() {
            // given
            given(commentMapper.findById(1L)).willReturn(testComment);

            // when
            commentService.deleteComment(1L, 1L, null, null);

            // then
            verify(commentMapper).delete(1L);
        }

        @Test
        @DisplayName("권한 없는 사용자 삭제 시 예외 발생")
        void deleteComment_accessDenied() {
            // given
            given(commentMapper.findById(1L)).willReturn(testComment);

            // when & then
            assertThatThrownBy(() -> commentService.deleteComment(1L, 999L, null, null))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_ACCESS_DENIED);
        }
    }
}
