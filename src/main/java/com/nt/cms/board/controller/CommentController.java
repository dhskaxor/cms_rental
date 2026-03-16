package com.nt.cms.board.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.service.CommentService;
import com.nt.cms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/boards/{boardId}/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 관리 API")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 목록 조회
     */
    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글 목록을 조회합니다 (대댓글 포함).")
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        List<CommentResponse> response = commentService.getComments(postId);
        return ApiResponse.success(response);
    }

    /**
     * 댓글 생성
     */
    @PostMapping
    @Operation(summary = "댓글 생성", description = "새로운 댓글을 생성합니다.")
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponse response = commentService.createComment(postId, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{id}")
    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다.")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponse response = commentService.updateComment(id, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getUserId(), userDetails.getRoleId(), boardId);
        return ApiResponse.success();
    }
}
