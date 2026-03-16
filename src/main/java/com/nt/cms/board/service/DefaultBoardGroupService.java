package com.nt.cms.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.mapper.BoardGroupMapper;
import com.nt.cms.board.mapper.BoardMapper;
import com.nt.cms.board.vo.BoardGroupVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 그룹 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultBoardGroupService implements BoardGroupService {

    private final BoardGroupMapper boardGroupMapper;
    private final BoardMapper boardMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<BoardGroupResponse> getGroups() {
        log.debug("게시판 그룹 목록 조회");

        List<BoardGroupVO> groups = boardGroupMapper.findAll();
        return groups.stream()
                .map(BoardGroupResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public BoardGroupResponse getGroup(Long id) {
        log.debug("게시판 그룹 상세 조회: {}", id);

        BoardGroupVO group = boardGroupMapper.findById(id);
        if (group == null) {
            throw new BusinessException(ErrorCode.BOARD_GROUP_NOT_FOUND);
        }

        return BoardGroupResponse.from(group);
    }

    @Override
    @Transactional
    public BoardGroupResponse createGroup(BoardGroupCreateRequest request, Long createdBy) {
        log.info("게시판 그룹 생성: {}", request.getGroupCode());

        // 중복 확인
        if (boardGroupMapper.existsByGroupCode(request.getGroupCode())) {
            throw new BusinessException(ErrorCode.BOARD_GROUP_ALREADY_EXISTS);
        }

        // 그룹 생성
        BoardGroupVO group = BoardGroupVO.builder()
                .groupCode(request.getGroupCode())
                .groupName(request.getGroupName())
                .createdBy(createdBy)
                .build();

        boardGroupMapper.insert(group);
        log.info("게시판 그룹 생성 완료: id={}", group.getId());

        recordAuditLog("CREATE", "BOARD_GROUP", group.getId(), null, group, createdBy);

        return getGroup(group.getId());
    }

    @Override
    @Transactional
    public BoardGroupResponse updateGroup(Long id, BoardGroupUpdateRequest request, Long updatedBy) {
        log.info("게시판 그룹 수정: id={}", id);

        // 존재 확인
        BoardGroupVO existingGroup = boardGroupMapper.findById(id);
        if (existingGroup == null) {
            throw new BusinessException(ErrorCode.BOARD_GROUP_NOT_FOUND);
        }

        // 그룹 수정
        BoardGroupVO group = BoardGroupVO.builder()
                .id(id)
                .groupName(request.getGroupName())
                .updatedBy(updatedBy)
                .build();

        boardGroupMapper.update(group);
        log.info("게시판 그룹 수정 완료: id={}", id);

        BoardGroupVO afterGroup = boardGroupMapper.findById(id);
        recordAuditLog("UPDATE", "BOARD_GROUP", id, existingGroup, afterGroup, updatedBy);

        return getGroup(id);
    }

    @Override
    @Transactional
    public void deleteGroup(Long id, Long deletedBy) {
        log.info("게시판 그룹 삭제: id={}", id);

        // 존재 확인
        BoardGroupVO group = boardGroupMapper.findById(id);
        if (group == null) {
            throw new BusinessException(ErrorCode.BOARD_GROUP_NOT_FOUND);
        }

        // 그룹에 속한 게시판 있는지 확인
        int boardCount = boardMapper.countByGroupId(id);
        if (boardCount > 0) {
            throw new BusinessException(ErrorCode.BOARD_GROUP_HAS_BOARDS);
        }

        boardGroupMapper.delete(id, deletedBy);
        log.info("게시판 그룹 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "BOARD_GROUP", id, group, null, deletedBy);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               BoardGroupVO before, BoardGroupVO after, Long actorId) {
        try {
            String username = userMapper.findUsernameById(actorId);
            String beforeJson = before != null ? objectMapper.writeValueAsString(before) : null;
            String afterJson = after != null ? objectMapper.writeValueAsString(after) : null;
            auditLogService.log(actorId, username, action, targetType, targetId, beforeJson, afterJson);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
        }
    }

    @Override
    public boolean isGroupCodeDuplicated(String groupCode) {
        return boardGroupMapper.existsByGroupCode(groupCode);
    }
}
