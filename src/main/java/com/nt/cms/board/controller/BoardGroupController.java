package com.nt.cms.board.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.service.BoardGroupService;
import com.nt.cms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시판 그룹 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/board-groups")
@RequiredArgsConstructor
@Tag(name = "게시판 그룹", description = "게시판 그룹 관리 API")
public class BoardGroupController {

    private final BoardGroupService boardGroupService;

    /**
     * 그룹 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_READ.value)")
    @Operation(summary = "그룹 목록 조회", description = "모든 게시판 그룹 목록을 조회합니다.")
    public ApiResponse<List<BoardGroupResponse>> getGroups() {
        List<BoardGroupResponse> response = boardGroupService.getGroups();
        return ApiResponse.success(response);
    }

    /**
     * 그룹 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_READ.value)")
    @Operation(summary = "그룹 상세 조회", description = "게시판 그룹 ID로 상세 정보를 조회합니다.")
    public ApiResponse<BoardGroupResponse> getGroup(@PathVariable Long id) {
        BoardGroupResponse response = boardGroupService.getGroup(id);
        return ApiResponse.success(response);
    }

    /**
     * 그룹 생성
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_CREATE.value)")
    @Operation(summary = "그룹 생성", description = "새로운 게시판 그룹을 생성합니다.")
    public ApiResponse<BoardGroupResponse> createGroup(
            @Valid @RequestBody BoardGroupCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoardGroupResponse response = boardGroupService.createGroup(request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 그룹 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_UPDATE.value)")
    @Operation(summary = "그룹 수정", description = "게시판 그룹 정보를 수정합니다.")
    public ApiResponse<BoardGroupResponse> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody BoardGroupUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoardGroupResponse response = boardGroupService.updateGroup(id, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 그룹 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_DELETE.value)")
    @Operation(summary = "그룹 삭제", description = "게시판 그룹을 삭제합니다.")
    public ApiResponse<Void> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardGroupService.deleteGroup(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 그룹 코드 중복 확인
     */
    @GetMapping("/check/code")
    @Operation(summary = "그룹 코드 중복 확인", description = "그룹 코드 중복 여부를 확인합니다.")
    public ApiResponse<Boolean> checkGroupCode(@RequestParam String groupCode) {
        boolean isDuplicated = boardGroupService.isGroupCodeDuplicated(groupCode);
        return ApiResponse.success(isDuplicated);
    }
}
