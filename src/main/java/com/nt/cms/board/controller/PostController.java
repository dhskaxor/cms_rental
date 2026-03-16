package com.nt.cms.board.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.service.PostService;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/boards/{boardId}/posts")
@RequiredArgsConstructor
@Tag(name = "게시글", description = "게시글 관리 API")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 목록 조회
     */
    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "게시판의 게시글 목록을 조회합니다.")
    public ApiResponse<PageResponse<PostResponse>> getPosts(
            @PathVariable Long boardId,
            @ModelAttribute PostSearchRequest request) {
        PageResponse<PostResponse> response = postService.getPosts(boardId, request);
        return ApiResponse.success(response);
    }

    /**
     * 공지글 목록 조회
     */
    @GetMapping("/notices")
    @Operation(summary = "공지글 목록 조회", description = "게시판의 공지글 목록을 조회합니다.")
    public ApiResponse<List<PostResponse>> getNotices(@PathVariable Long boardId) {
        List<PostResponse> response = postService.getNotices(boardId);
        return ApiResponse.success(response);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    public ApiResponse<PostResponse> getPost(
            @PathVariable Long boardId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUserId() : null;
        Long roleId = userDetails != null ? userDetails.getRoleId() : null;
        PostResponse response = postService.getPost(id, userId, roleId);
        return ApiResponse.success(response);
    }

    /**
     * 게시글 생성
     */
    @PostMapping
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    public ApiResponse<PostResponse> createPost(
            @PathVariable Long boardId,
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostResponse response = postService.createPost(
                boardId, request, userDetails.getUserId(), userDetails.getRoleId());
        return ApiResponse.success(response);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "게시글 정보를 수정합니다.")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long boardId,
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostResponse response = postService.updatePost(
                id, request, userDetails.getUserId(), userDetails.getRoleId());
        return ApiResponse.success(response);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ApiResponse<Void> deletePost(
            @PathVariable Long boardId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deletePost(id, userDetails.getUserId(), userDetails.getRoleId());
        return ApiResponse.success();
    }

    /**
     * 이전 게시글 조회
     */
    @GetMapping("/{id}/prev")
    @Operation(summary = "이전 게시글", description = "이전 게시글을 조회합니다.")
    public ApiResponse<PostResponse> getPrevPost(
            @PathVariable Long boardId,
            @PathVariable Long id) {
        PostResponse response = postService.getPrevPost(boardId, id);
        return ApiResponse.success(response);
    }

    /**
     * 다음 게시글 조회
     */
    @GetMapping("/{id}/next")
    @Operation(summary = "다음 게시글", description = "다음 게시글을 조회합니다.")
    public ApiResponse<PostResponse> getNextPost(
            @PathVariable Long boardId,
            @PathVariable Long id) {
        PostResponse response = postService.getNextPost(boardId, id);
        return ApiResponse.success(response);
    }
}
