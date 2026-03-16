package com.nt.cms.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.nt.cms.file.service.FileService;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.nt.cms.common.constant.SiteConstant.ROLE_ANONYMOUS;
import static com.nt.cms.common.constant.SiteConstant.USER_ANONYMOUS;

/**
 * 게시글 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultPostService implements PostService {

    private final PostMapper postMapper;
    private final BoardMapper boardMapper;
    private final FileService fileService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<PostResponse> getPosts(Long boardId, PostSearchRequest request) {
        log.debug("게시글 목록 조회: boardId={}", boardId);

        // 게시판 존재 확인
        BoardVO board = boardMapper.findById(boardId);
        if (board == null) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        List<PostVO> posts = postMapper.findByBoardId(boardId, request);
        long totalElements = postMapper.countByBoardId(boardId, request);

        List<PostResponse> content = posts.stream()
                .map(PostResponse::fromList)
                .collect(Collectors.toList());

        return PageResponse.of(content, request.getPage(), request.getSize(), totalElements);
    }

    @Override
    public List<PostResponse> getNotices(Long boardId) {
        log.debug("공지글 목록 조회: boardId={}", boardId);

        List<PostVO> notices = postMapper.findNoticesByBoardId(boardId);
        return notices.stream()
                .map(PostResponse::fromList)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostResponse getPost(Long id, Long userId, Long roleId) {
        log.debug("게시글 상세 조회: id={}", id);

        PostVO post = postMapper.findById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        // 비밀글 접근 확인 (roleId=null 또는 userId=null인 익명 사용자 차단)
        if (Boolean.TRUE.equals(post.getIsSecret())) {
            // 작성자이거나 삭제 권한이 있는 관리자만 접근 가능
            boolean isWriter = Objects.equals(post.getWriterId(), userId);
            boolean hasDeletePermission = checkPermission(post.getBoardId(), roleId, "delete");
            if (!isWriter && !hasDeletePermission) {
                throw new BusinessException(ErrorCode.SECRET_POST_ACCESS_DENIED);
            }
        }

        // 조회수 증가
        postMapper.increaseViewCount(id);

        return PostResponse.from(post);
    }

    @Override
    @Transactional
    public PostResponse createPost(Long boardId, PostCreateRequest request, Long userId, Long roleId) {
        log.info("게시글 생성: boardId={}", boardId);

        // 게시판 존재 확인
        BoardVO board = boardMapper.findById(boardId);
        if (board == null) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        // 권한 확인 (roleId null이면 비회원 → ANONYMOUS 역할로 조회)
        if (!checkPermission(boardId, roleId, "create")) {
            throw new BusinessException(ErrorCode.BOARD_PERMISSION_DENIED);
        }

        // 비밀글 사용 가능 여부 확인
        boolean isSecret = Boolean.TRUE.equals(request.getIsSecret());
        if (isSecret && !Boolean.TRUE.equals(board.getUseSecret())) {
            isSecret = false;
        }

        // 비로그인 사용자는 시스템 익명 계정으로 저장
        Long effectiveUserId = resolveWriterId(userId);

        // 게시글 생성
        PostVO post = PostVO.builder()
                .boardId(boardId)
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(effectiveUserId)
                .isNotice(request.getIsNotice())
                .isSecret(isSecret)
                .createdBy(userId)
                .build();

        postMapper.insert(post);
        log.info("게시글 생성 완료: id={}", post.getId());

        // 에디터 인라인 이미지 ref_id 갱신
        if (request.getAttachedFileIds() != null && !request.getAttachedFileIds().isEmpty()) {
            for (Long fileId : request.getAttachedFileIds()) {
                if (fileId != null && fileId > 0) {
                    try {
                        fileService.updateRefId(fileId, post.getId());
                    } catch (Exception e) {
                        log.warn("에디터 이미지 ref_id 갱신 실패: fileId={}, postId={}", fileId, post.getId(), e);
                    }
                }
            }
        }

        PostVO savedPost = postMapper.findById(post.getId());
        recordAuditLog("CREATE", "POST", savedPost.getId(), null, savedPost, effectiveUserId);

        return PostResponse.from(savedPost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostUpdateRequest request, Long userId, Long roleId) {
        log.info("게시글 수정: id={}", id);

        PostVO post = postMapper.findById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        // 작성자이거나 수정 권한이 있는지 확인
        boolean isWriter = post.getWriterId().equals(userId);
        boolean hasUpdatePermission = checkPermission(post.getBoardId(), roleId, "update");
        if (!isWriter && !hasUpdatePermission) {
            throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
        }

        // 게시글 수정
        PostVO updatePost = PostVO.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .isNotice(request.getIsNotice() != null ? request.getIsNotice() : post.getIsNotice())
                .isSecret(request.getIsSecret() != null ? request.getIsSecret() : post.getIsSecret())
                .updatedBy(userId)
                .build();

        postMapper.update(updatePost);
        log.info("게시글 수정 완료: id={}", id);

        // 에디터 인라인 이미지 ref_id 갱신
        if (request.getAttachedFileIds() != null && !request.getAttachedFileIds().isEmpty()) {
            for (Long fileId : request.getAttachedFileIds()) {
                if (fileId != null && fileId > 0) {
                    try {
                        fileService.updateRefId(fileId, id);
                    } catch (Exception e) {
                        log.warn("에디터 이미지 ref_id 갱신 실패: fileId={}, postId={}", fileId, id, e);
                    }
                }
            }
        }

        PostVO afterPost = postMapper.findById(id);
        recordAuditLog("UPDATE", "POST", id, post, afterPost, userId);

        return PostResponse.from(afterPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long userId, Long roleId) {
        log.info("게시글 삭제: id={}", id);

        PostVO post = postMapper.findById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        // 작성자이거나 삭제 권한이 있는지 확인
        boolean isWriter = post.getWriterId().equals(userId);
        boolean hasDeletePermission = checkPermission(post.getBoardId(), roleId, "delete");
        if (!isWriter && !hasDeletePermission) {
            throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
        }

        postMapper.delete(id, userId);
        log.info("게시글 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "POST", id, post, null, userId);
    }

    @Override
    public PostResponse getPrevPost(Long boardId, Long id) {
        PostVO prev = postMapper.findPrevPost(boardId, id);
        return prev != null ? PostResponse.fromList(prev) : null;
    }

    @Override
    public PostResponse getNextPost(Long boardId, Long id) {
        PostVO next = postMapper.findNextPost(boardId, id);
        return next != null ? PostResponse.fromList(next) : null;
    }

    @Override
    public List<PostResponse> getLatestPosts(LatestPostsRequest request) {
        log.debug("최신글 목록 조회: boardIds={}, size={}, sortField={}, sortOrder={}",
                request.getBoardIds(), request.getSize(), request.getSortField(), request.getSortOrder());

        if (request.getBoardIds() == null || request.getBoardIds().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "게시판 ID 목록은 필수입니다.");
        }

        List<PostVO> posts = postMapper.findLatestPosts(
                request.getBoardIds(),
                request.getValidSize(),
                request.getValidSortField(),
                request.getValidSortOrder()
        );

        return posts.stream()
                .map(PostResponse::fromList)
                .collect(Collectors.toList());
    }

    /**
     * 감사 로그 기록
     */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               PostVO before, PostVO after, Long actorId) {
        try {
            String actorUsername = userMapper.findUsernameById(actorId);
            String beforeJson = before != null ? objectMapper.writeValueAsString(toAuditMap(before)) : null;
            String afterJson = after != null ? objectMapper.writeValueAsString(toAuditMap(after)) : null;
            auditLogService.log(actorId, actorUsername, action, targetType, targetId, beforeJson, afterJson);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
        }
    }

    /** 게시글 감사용 Map 생성 (content 500자 제한) */
    private Map<String, Object> toAuditMap(PostVO post) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", post.getId());
        map.put("boardId", post.getBoardId());
        map.put("title", post.getTitle());
        String content = post.getContent();
        map.put("content", content != null && content.length() > 500 ? content.substring(0, 500) + "..." : content);
        map.put("writerId", post.getWriterId());
        map.put("isNotice", post.getIsNotice());
        map.put("isSecret", post.getIsSecret());
        return map;
    }

    /**
     * 비로그인 시 시스템 익명 계정 ID 반환
     */
    private Long resolveWriterId(Long userId) {
        if (userId != null) {
            return userId;
        }
        UserVO anonymous = userMapper.findByUsername(USER_ANONYMOUS);
        if (anonymous == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "익명 게시글 작성 시 시스템 익명 계정이 필요합니다.");
        }
        return anonymous.getId();
    }

    /**
     * 비회원용 역할 ID 반환 (roleId null 시)
     */
    private Long resolveRoleId(Long roleId) {
        if (roleId != null) {
            return roleId;
        }
        RoleVO anonymous = roleMapper.findByRoleCode(ROLE_ANONYMOUS);
        return anonymous != null ? anonymous.getId() : null;
    }

    /**
     * 권한 확인 (roleId null이면 ANONYMOUS 역할로 조회)
     */
    private boolean checkPermission(Long boardId, Long roleId, String action) {
        Long effectiveRoleId = resolveRoleId(roleId);
        if (effectiveRoleId == null) {
            return false;
        }
        BoardPermissionVO permission = boardMapper.findPermissionByBoardIdAndRoleId(boardId, effectiveRoleId);
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
}
