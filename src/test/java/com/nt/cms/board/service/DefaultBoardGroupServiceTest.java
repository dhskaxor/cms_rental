package com.nt.cms.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.mapper.BoardGroupMapper;
import com.nt.cms.board.mapper.BoardMapper;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.board.vo.BoardGroupVO;
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
 * DefaultBoardGroupService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultBoardGroupService 테스트")
class DefaultBoardGroupServiceTest {

    @Mock
    private BoardGroupMapper boardGroupMapper;

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultBoardGroupService boardGroupService;

    private BoardGroupVO testGroup;

    @BeforeEach
    void setUp() {
        testGroup = BoardGroupVO.builder()
                .id(1L)
                .groupCode("notice")
                .groupName("공지사항")
                .boardCount(2)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("그룹 목록 조회")
    class GetGroupsTest {

        @Test
        @DisplayName("그룹 목록 조회 성공")
        void getGroups_success() {
            // given
            given(boardGroupMapper.findAll()).willReturn(Arrays.asList(testGroup));

            // when
            List<BoardGroupResponse> response = boardGroupService.getGroups();

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getGroupCode()).isEqualTo("notice");
        }

        @Test
        @DisplayName("그룹이 없을 때 빈 목록 반환")
        void getGroups_empty() {
            // given
            given(boardGroupMapper.findAll()).willReturn(Collections.emptyList());

            // when
            List<BoardGroupResponse> response = boardGroupService.getGroups();

            // then
            assertThat(response).isEmpty();
        }
    }

    @Nested
    @DisplayName("그룹 상세 조회")
    class GetGroupTest {

        @Test
        @DisplayName("ID로 그룹 조회 성공")
        void getGroup_success() {
            // given
            given(boardGroupMapper.findById(1L)).willReturn(testGroup);

            // when
            BoardGroupResponse response = boardGroupService.getGroup(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getGroupCode()).isEqualTo("notice");
        }

        @Test
        @DisplayName("존재하지 않는 그룹 조회 시 예외 발생")
        void getGroup_notFound() {
            // given
            given(boardGroupMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> boardGroupService.getGroup(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_GROUP_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("그룹 생성")
    class CreateGroupTest {

        @Test
        @DisplayName("그룹 생성 성공")
        void createGroup_success() {
            // given
            BoardGroupCreateRequest request = BoardGroupCreateRequest.builder()
                    .groupCode("community")
                    .groupName("커뮤니티")
                    .build();

            given(boardGroupMapper.existsByGroupCode("community")).willReturn(false);
            given(boardGroupMapper.insert(any(BoardGroupVO.class))).willAnswer(invocation -> {
                BoardGroupVO group = invocation.getArgument(0);
                group.setId(2L);
                return 1;
            });
            given(boardGroupMapper.findById(2L)).willReturn(BoardGroupVO.builder()
                    .id(2L)
                    .groupCode("community")
                    .groupName("커뮤니티")
                    .build());

            // when
            BoardGroupResponse response = boardGroupService.createGroup(request, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getGroupCode()).isEqualTo("community");
        }

        @Test
        @DisplayName("중복된 그룹 코드일 때 예외 발생")
        void createGroup_duplicate() {
            // given
            BoardGroupCreateRequest request = BoardGroupCreateRequest.builder()
                    .groupCode("notice")
                    .groupName("공지")
                    .build();

            given(boardGroupMapper.existsByGroupCode("notice")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> boardGroupService.createGroup(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_GROUP_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("그룹 삭제")
    class DeleteGroupTest {

        @Test
        @DisplayName("그룹 삭제 성공")
        void deleteGroup_success() {
            // given
            BoardGroupVO emptyGroup = BoardGroupVO.builder()
                    .id(1L)
                    .groupCode("empty")
                    .boardCount(0)
                    .build();

            given(boardGroupMapper.findById(1L)).willReturn(emptyGroup);
            given(boardMapper.countByGroupId(1L)).willReturn(0);
            given(boardGroupMapper.delete(1L, 1L)).willReturn(1);

            // when
            boardGroupService.deleteGroup(1L, 1L);

            // then
            verify(boardGroupMapper).delete(1L, 1L);
        }

        @Test
        @DisplayName("게시판이 있는 그룹 삭제 시 예외 발생")
        void deleteGroup_hasBoards() {
            // given
            given(boardGroupMapper.findById(1L)).willReturn(testGroup);
            given(boardMapper.countByGroupId(1L)).willReturn(2);

            // when & then
            assertThatThrownBy(() -> boardGroupService.deleteGroup(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BOARD_GROUP_HAS_BOARDS);
        }
    }
}
