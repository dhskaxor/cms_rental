package com.nt.cms.publicapi.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.board.dto.BoardGroupResponse;
import com.nt.cms.board.dto.BoardResponse;
import com.nt.cms.board.dto.CommentResponse;
import com.nt.cms.board.dto.LatestPostsRequest;
import com.nt.cms.board.dto.PostCreateRequest;
import com.nt.cms.board.dto.PostResponse;
import com.nt.cms.board.dto.PostSearchRequest;
import com.nt.cms.board.dto.PostUpdateRequest;
import com.nt.cms.board.service.BoardGroupService;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.CommentService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자단 공개 게시판 API 컨트롤러
 *
 * <p>게시판 그룹, 게시판, 게시글, 댓글을 인증 없이 조회한다.</p>
 * <p>비밀글은 roleId=null로 조회 시 차단된다 (4단계에서 PostService 보완).</p>
 *
 * @author CMS Team
 */
@Tag(name = "Public Board", description = "사용자단 공개 게시판 API (인증 불필요)")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicBoardController {

    private final BoardGroupService boardGroupService;
    private final BoardService boardService;
    private final PostService postService;
    private final CommentService commentService;
    private final RoleMapper roleMapper;

    /**
     * 게시판 그룹 목록 조회
     */
    @Operation(summary = "게시판 그룹 목록", description = "게시판 그룹 목록을 조회합니다.")
    @GetMapping("/board-groups")
    public ApiResponse<List<BoardGroupResponse>> getBoardGroups() {
        return ApiResponse.success(boardGroupService.getGroups());
    }

    /**
     * 게시판 목록 조회
     */
    @Operation(summary = "게시판 목록", description = "전체 게시판 목록을 조회합니다.")
    @GetMapping("/boards")
    public ApiResponse<List<BoardResponse>> getBoards() {
        return ApiResponse.success(boardService.getBoards());
    }

    /**
     * 그룹별 게시판 목록 조회
     */
    @Operation(summary = "그룹별 게시판 목록", description = "지정한 그룹의 게시판 목록을 조회합니다.")
    @GetMapping("/boards/group/{groupId}")
    public ApiResponse<List<BoardResponse>> getBoardsByGroupId(@PathVariable Long groupId) {
        return ApiResponse.success(boardService.getBoardsByGroupId(groupId));
    }

    /**
     * 게시판 ID로 게시판 조회 (사용자 사이트 템플릿 등 활용)
     */
    @Operation(summary = "게시판 상세 (ID)", description = "게시판 ID로 게시판 정보를 조회합니다.")
    @GetMapping("/boards/{boardId}")
    public ApiResponse<BoardResponse> getBoardById(@PathVariable Long boardId) {
        return ApiResponse.success(boardService.getBoard(boardId));
    }

    /**
     * 게시판 코드로 게시판 조회
     */
    @Operation(summary = "게시판 상세 (코드)", description = "게시판 코드로 게시판 정보를 조회합니다.")
    @GetMapping("/boards/code/{boardCode}")
    public ApiResponse<BoardResponse> getBoardByCode(@PathVariable String boardCode) {
        return ApiResponse.success(boardService.getBoardByCode(boardCode));
    }

    /**
     * 게시글 목록 조회 (페이징, 검색)
     */
    @Operation(summary = "게시글 목록", description = "게시판의 게시글 목록을 페이징/검색하여 조회합니다.")
    @GetMapping("/boards/{boardId}/posts")
    public ApiResponse<PageResponse<PostResponse>> getPosts(
            @PathVariable Long boardId,
            @ModelAttribute PostSearchRequest request) {
        return ApiResponse.success(postService.getPosts(boardId, request));
    }

    /**
     * 최신글 목록 조회 (여러 게시판)
     * 
     * <p>여러 게시판의 최신글을 한 번에 조회합니다.</p>
     * <p>비밀글과 공지글은 제외됩니다.</p>
     */
    @Operation(summary = "최신글 목록", description = "여러 게시판의 최신글을 조회합니다. boardIds는 콤마로 구분합니다.")
    @GetMapping("/posts/latest")
    public ApiResponse<List<PostResponse>> getLatestPosts(@ModelAttribute LatestPostsRequest request) {
        return ApiResponse.success(postService.getLatestPosts(request));
    }

    /**
     * 공지글 목록 조회
     */
    @Operation(summary = "공지글 목록", description = "게시판의 공지글 목록을 조회합니다.")
    @GetMapping("/boards/{boardId}/posts/notices")
    public ApiResponse<List<PostResponse>> getNotices(@PathVariable Long boardId) {
        return ApiResponse.success(postService.getNotices(boardId));
    }

    /**
     * 게시글 상세 조회 (비밀글 시 익명 사용자 차단)
     */
    @Operation(summary = "게시글 상세", description = "게시글 상세를 조회합니다. 비밀글은 인증 없이 조회 불가.")
    @GetMapping("/boards/{boardId}/posts/{postId}")
    public ApiResponse<PostResponse> getPost(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        PostResponse response = postService.getPost(postId, null, null);
        return ApiResponse.success(response);
    }

    /**
     * 이전 게시글 조회
     */
    @Operation(summary = "이전 게시글", description = "현재 게시글의 이전 게시글을 조회합니다.")
    @GetMapping("/boards/{boardId}/posts/{postId}/prev")
    public ApiResponse<PostResponse> getPrevPost(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        return ApiResponse.success(postService.getPrevPost(boardId, postId));
    }

    /**
     * 다음 게시글 조회
     */
    @Operation(summary = "다음 게시글", description = "현재 게시글의 다음 게시글을 조회합니다.")
    @GetMapping("/boards/{boardId}/posts/{postId}/next")
    public ApiResponse<PostResponse> getNextPost(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        return ApiResponse.success(postService.getNextPost(boardId, postId));
    }

    /**
     * 댓글 목록 조회
     */
    @Operation(summary = "댓글 목록", description = "게시글의 댓글 목록을 조회합니다 (대댓글 포함).")
    @GetMapping("/boards/{boardId}/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        return ApiResponse.success(commentService.getComments(postId));
    }

    /**
     * 게시글 작성 (인증 선택: 로그인 시 사용자, 비로그인 시 ANONYMOUS 권한으로 처리)
     */
    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다. 비로그인 시 비회원 권한으로 처리됩니다.")
    @PostMapping("/boards/{boardId}/posts")
    public ApiResponse<PostResponse> createPost(
            @PathVariable Long boardId,
            @Valid @RequestBody PostCreateRequest request,
            HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);
        PostResponse response = postService.createPost(boardId, request, userId, roleId);
        return ApiResponse.success(response);
    }

    /**
     * 게시글 수정 (인증 선택: 로그인 시 사용자, 비로그인 시 ANONYMOUS 권한으로 처리)
     */
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/boards/{boardId}/posts/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);
        PostResponse response = postService.updatePost(postId, request, userId, roleId);
        return ApiResponse.success(response);
    }

    /**
     * 게시글 삭제 (인증 선택: 로그인 시 사용자, 비로그인 시 ANONYMOUS 권한으로 처리)
     */
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/boards/{boardId}/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);
        postService.deletePost(postId, userId, roleId);
        return ApiResponse.success();
    }

    /**
     * SessionUser에서 roleId 추출 (비로그인 시 ANONYMOUS 역할)
     */
    private Long resolveRoleId(SessionUser user) {
        String roleCode = (user != null && user.getRoleCode() != null) ? user.getRoleCode() : "ANONYMOUS";
        RoleVO role = roleMapper.findByRoleCode(roleCode);
        return role != null ? role.getId() : null;
    }
}
