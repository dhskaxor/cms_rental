package com.nt.cms.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.mapper.BoardMapper;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.board.vo.BoardPermissionVO;
import com.nt.cms.board.vo.BoardVO;
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
 * DefaultBoardService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultBoardService 테스트")
class DefaultBoardServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultBoardService boardService;

    private BoardVO testBoard;

    @BeforeEach
    void setUp() {
        testBoard = BoardVO.builder()
                .id(1L)
                .boardCode("free")
                .boardName("자유게시판")
                .groupId(1L)
                .groupName("커뮤니티")
                .useComment(true)
                .useFile(true)
                .maxFileCount(5)
                .useSecret(false)
                .pageSize(10)
                .postCount(100L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("게시판 목록 조회")
    class GetBoardsTest {

        @Test
        @DisplayName("게시판 목록 조회 성공")
        void getBoards_success() {
            // given
            given(boardMapper.findAll()).willReturn(Arrays.asList(testBoard));
            given(boardMapper.findPermissionsByBoardId(1L)).willReturn(Collections.emptyList());

            // when
            List<BoardResponse> response = boardService.getBoards();

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getBoardCode()).isEqualTo("free");
        }
    }

    @Nested
    @DisplayName("게시판 상세 조회")
    class GetBoardTest {

        @Test
        @DisplayName("ID로 게시판 조회 성공")
        void getBoard_success() {
            // given
            given(boardMapper.findById(1L)).willReturn(testBoard);
            given(boardMapper.findPermissionsByBoardId(1L)).willReturn(Collections.emptyList());

            // when
            BoardResponse response = boardService.getBoard(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getBoardCode()).isEqualTo("free");
        }

        @Test
        @DisplayName("존재하지 않는 게시판 조회 시 예외 발생")
        void getBoard_notFound() {
            // given
            given(boardMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> boardService.getBoard(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시판 생성")
    class CreateBoardTest {

        @Test
        @DisplayName("게시판 생성 성공")
        void createBoard_success() {
            // given
            BoardCreateRequest request = BoardCreateRequest.builder()
                    .boardCode("qna")
                    .boardName("Q&A")
                    .groupId(1L)
                    .useComment(true)
                    .useFile(true)
                    .maxFileCount(3)
                    .useSecret(false)
                    .pageSize(15)
                    .build();

            given(boardMapper.existsByBoardCode("qna")).willReturn(false);
            given(boardMapper.insert(any(BoardVO.class))).willAnswer(invocation -> {
                BoardVO board = invocation.getArgument(0);
                board.setId(2L);
                return 1;
            });
            given(boardMapper.findById(2L)).willReturn(BoardVO.builder()
                    .id(2L)
                    .boardCode("qna")
                    .boardName("Q&A")
                    .build());
            given(boardMapper.findPermissionsByBoardId(2L)).willReturn(Collections.emptyList());

            // when
            BoardResponse response = boardService.createBoard(request, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getBoardCode()).isEqualTo("qna");
        }

        @Test
        @DisplayName("중복된 게시판 코드일 때 예외 발생")
        void createBoard_duplicate() {
            // given
            BoardCreateRequest request = BoardCreateRequest.builder()
                    .boardCode("free")
                    .boardName("자유게시판")
                    .build();

            given(boardMapper.existsByBoardCode("free")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> boardService.createBoard(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("게시판 삭제")
    class DeleteBoardTest {

        @Test
        @DisplayName("게시판 삭제 성공")
        void deleteBoard_success() {
            // given
            BoardVO emptyBoard = BoardVO.builder()
                    .id(1L)
                    .boardCode("empty")
                    .postCount(0L)
                    .build();

            given(boardMapper.findById(1L)).willReturn(emptyBoard);
            given(boardMapper.delete(1L, 1L)).willReturn(1);

            // when
            boardService.deleteBoard(1L, 1L);

            // then
            verify(boardMapper).deletePermissions(1L);
            verify(boardMapper).delete(1L, 1L);
        }

        @Test
        @DisplayName("게시글이 있는 게시판 삭제 시 예외 발생")
        void deleteBoard_hasPosts() {
            // given
            given(boardMapper.findById(1L)).willReturn(testBoard);

            // when & then
            assertThatThrownBy(() -> boardService.deleteBoard(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_HAS_POSTS);
        }
    }

    @Nested
    @DisplayName("권한 확인")
    class PermissionCheckTest {

        @Test
        @DisplayName("권한이 있는 경우 true 반환")
        void hasPermission_true() {
            // given
            BoardPermissionVO permission = BoardPermissionVO.builder()
                    .boardId(1L)
                    .roleId(1L)
                    .canCreate(true)
                    .canRead(true)
                    .canUpdate(false)
                    .canDelete(false)
                    .build();

            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 1L)).willReturn(permission);

            // when
            boolean result = boardService.hasPermission(1L, 1L, "create");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("권한이 없는 경우 false 반환")
        void hasPermission_false() {
            // given
            given(boardMapper.findPermissionByBoardIdAndRoleId(1L, 1L)).willReturn(null);

            // when
            boolean result = boardService.hasPermission(1L, 1L, "create");

            // then
            assertThat(result).isFalse();
        }
    }
}
