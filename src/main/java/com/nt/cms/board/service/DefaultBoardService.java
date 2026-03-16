package com.nt.cms.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.mapper.BoardMapper;
import com.nt.cms.board.vo.BoardPermissionVO;
import com.nt.cms.board.vo.BoardVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultBoardService implements BoardService {

    private final BoardMapper boardMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourcePatternResolver;

    @Override
    public List<BoardResponse> getBoards() {
        log.debug("게시판 목록 조회");

        List<BoardVO> boards = boardMapper.findAll();
        return boards.stream()
                .map(this::toResponseWithPermissions)
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardResponse> getBoardsByGroupId(Long groupId) {
        log.debug("그룹별 게시판 목록 조회: groupId={}", groupId);

        List<BoardVO> boards = boardMapper.findByGroupId(groupId);
        return boards.stream()
                .map(this::toResponseWithPermissions)
                .collect(Collectors.toList());
    }

    @Override
    public BoardResponse getBoard(Long id) {
        log.debug("게시판 상세 조회: {}", id);

        BoardVO board = boardMapper.findById(id);
        if (board == null) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        return toResponseWithPermissions(board);
    }

    @Override
    public BoardResponse getBoardByCode(String boardCode) {
        log.debug("게시판 코드로 조회: {}", boardCode);

        BoardVO board = boardMapper.findByBoardCode(boardCode);
        if (board == null) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        return toResponseWithPermissions(board);
    }

    @Override
    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request, Long createdBy) {
        log.info("게시판 생성: {}", request.getBoardCode());

        // 중복 확인
        if (boardMapper.existsByBoardCode(request.getBoardCode())) {
            throw new BusinessException(ErrorCode.BOARD_ALREADY_EXISTS);
        }

        // 게시판 생성
        BoardVO board = BoardVO.builder()
                .boardCode(request.getBoardCode())
                .boardName(request.getBoardName())
                .groupId(request.getGroupId())
                .useComment(request.getUseComment())
                .useFile(request.getUseFile())
                .maxFileCount(request.getMaxFileCount())
                .useSecret(request.getUseSecret())
                .pageSize(request.getPageSize())
                .templateCode(request.getTemplateCode() != null ? request.getTemplateCode() : "default")
                .useEditor(request.getUseEditor() != null ? request.getUseEditor() : false)
                .createdBy(createdBy)
                .build();

        boardMapper.insert(board);
        log.info("게시판 생성 완료: id={}", board.getId());

        BoardVO savedBoard = boardMapper.findById(board.getId());
        recordAuditLog("CREATE", "BOARD", savedBoard.getId(), null, savedBoard, createdBy);

        // 권한 설정
        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            for (BoardPermissionRequest permRequest : request.getPermissions()) {
                BoardPermissionVO permission = BoardPermissionVO.builder()
                        .boardId(board.getId())
                        .roleId(permRequest.getRoleId())
                        .canCreate(permRequest.getCanCreate())
                        .canRead(permRequest.getCanRead())
                        .canUpdate(permRequest.getCanUpdate())
                        .canDelete(permRequest.getCanDelete())
                        .build();
                boardMapper.insertPermission(permission);
            }
            log.debug("게시판 권한 설정 완료: boardId={}", board.getId());
        }

        return getBoard(board.getId());
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(Long id, BoardUpdateRequest request, Long updatedBy) {
        log.info("게시판 수정: id={}", id);

        // 존재 확인
        BoardVO existingBoard = boardMapper.findById(id);
        if (existingBoard == null) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        // 게시판 수정
        BoardVO board = BoardVO.builder()
                .id(id)
                .boardName(request.getBoardName())
                .groupId(request.getGroupId())
                .useComment(request.getUseComment() != null ? request.getUseComment() : existingBoard.getUseComment())
                .useFile(request.getUseFile() != null ? request.getUseFile() : existingBoard.getUseFile())
                .maxFileCount(request.getMaxFileCount() != null ? request.getMaxFileCount() : existingBoard.getMaxFileCount())
                .useSecret(request.getUseSecret() != null ? request.getUseSecret() : existingBoard.getUseSecret())
                .pageSize(request.getPageSize() != null ? request.getPageSize() : existingBoard.getPageSize())
                .templateCode(request.getTemplateCode() != null ? request.getTemplateCode() : existingBoard.getTemplateCode())
                .useEditor(request.getUseEditor() != null ? request.getUseEditor() : existingBoard.getUseEditor())
                .updatedBy(updatedBy)
                .build();

        boardMapper.update(board);
        log.info("게시판 수정 완료: id={}", id);

        BoardVO afterBoard = boardMapper.findById(id);
        recordAuditLog("UPDATE", "BOARD", id, existingBoard, afterBoard, updatedBy);

        // 권한 설정 업데이트
        if (request.getPermissions() != null) {
            boardMapper.deletePermissions(id);
            for (BoardPermissionRequest permRequest : request.getPermissions()) {
                BoardPermissionVO permission = BoardPermissionVO.builder()
                        .boardId(id)
                        .roleId(permRequest.getRoleId())
                        .canCreate(permRequest.getCanCreate())
                        .canRead(permRequest.getCanRead())
                        .canUpdate(permRequest.getCanUpdate())
                        .canDelete(permRequest.getCanDelete())
                        .build();
                boardMapper.insertPermission(permission);
            }
            log.debug("게시판 권한 업데이트 완료: boardId={}", id);
        }

        return getBoard(id);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id, Long deletedBy) {
        log.info("게시판 삭제: id={}", id);

        // 존재 확인
        BoardVO board = boardMapper.findById(id);
        if (board == null) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        // 게시글이 있는지 확인
        if (board.getPostCount() != null && board.getPostCount() > 0) {
            throw new BusinessException(ErrorCode.BOARD_HAS_POSTS);
        }

        // 권한 삭제
        boardMapper.deletePermissions(id);

        // 게시판 삭제
        boardMapper.delete(id, deletedBy);
        log.info("게시판 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "BOARD", id, board, null, deletedBy);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               BoardVO before, BoardVO after, Long actorId) {
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
    public boolean isBoardCodeDuplicated(String boardCode) {
        return boardMapper.existsByBoardCode(boardCode);
    }

    @Override
    public boolean hasPermission(Long boardId, Long roleId, String action) {
        BoardPermissionVO permission = boardMapper.findPermissionByBoardIdAndRoleId(boardId, roleId);
        if (permission == null) {
            return false;
        }

        return switch (action.toLowerCase()) {
            case "create" -> Boolean.TRUE.equals(permission.getCanCreate());
            case "read" -> Boolean.TRUE.equals(permission.getCanRead());
            case "update" -> Boolean.TRUE.equals(permission.getCanUpdate());
            case "delete" -> Boolean.TRUE.equals(permission.getCanDelete());
            default -> false;
        };
    }

    @Override
    public List<String> getAvailableTemplateCodes() {
        try {
            // templates/site/board/ 하위 폴더명을 템플릿 코드로 사용 (폴더 추가 시 관리자 셀렉트에 자동 반영)
            Resource[] resources = resourcePatternResolver.getResources(
                    "classpath*:templates/site/board/*/*.html");
            List<String> codes = new ArrayList<>();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("board/([^/]+)/[^/]+\\.html$");
            for (Resource r : resources) {
                try {
                    String path = r.getURI().toString().replace('\\', '/');
                    java.util.regex.Matcher m = pattern.matcher(path);
                    if (m.find() && !m.group(1).isEmpty()) {
                        String code = m.group(1);
                        if (!codes.contains(code)) {
                            codes.add(code);
                        }
                    }
                } catch (Exception e) {
                    log.debug("리소스 경로 파싱 건너뜀: {}", r);
                }
            }
            codes.sort(String::compareTo);
            return codes.isEmpty() ? List.of("default") : codes;
        } catch (IOException e) {
            log.warn("게시판 템플릿 목록 조회 실패: {}", e.getMessage());
            return List.of("default");
        }
    }

    @Override
    public String resolveBoardListView(String templateCode) {
        String code = (templateCode != null && !templateCode.isBlank()) ? templateCode.trim() : "default";
        return resolveView("site/board/" + code + "/board-list",
                "site/board/list");
    }

    @Override
    public String resolvePostDetailView(String templateCode) {
        String code = (templateCode != null && !templateCode.isBlank()) ? templateCode.trim() : "default";
        return resolveView("site/board/" + code + "/board-post",
                "site/board/post-detail");
    }

    @Override
    public String resolvePostFormView(String templateCode) {
        String code = (templateCode != null && !templateCode.isBlank()) ? templateCode.trim() : "default";
        return resolveView("site/board/" + code + "/post-form",
                "site/board/post-form");
    }

    /**
     * 템플릿 뷰 존재 여부 확인 후 적절한 뷰 이름 반환
     */
    private String resolveView(String... candidates) {
        for (String viewName : candidates) {
            if (existsTemplate(viewName)) {
                return viewName;
            }
        }
        return candidates[candidates.length - 1];
    }

    /**
     * Thymeleaf 템플릿 파일 존재 여부 확인
     */
    private boolean existsTemplate(String viewName) {
        try {
            String path = "classpath:templates/" + viewName + ".html";
            Resource[] resources = resourcePatternResolver.getResources(path);
            return resources != null && resources.length > 0 && resources[0].exists();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 권한 포함 Response 변환
     */
    private BoardResponse toResponseWithPermissions(BoardVO board) {
        List<BoardPermissionVO> permissions = boardMapper.findPermissionsByBoardId(board.getId());
        board.setPermissions(permissions);
        return BoardResponse.from(board);
    }
}
