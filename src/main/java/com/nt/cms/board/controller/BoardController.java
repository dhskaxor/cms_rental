package com.nt.cms.board.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.board.dto.*;
import com.nt.cms.board.service.BoardService;
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
 * 게시판 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
@Tag(name = "게시판", description = "게시판 관리 API")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시판 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_READ.value)")
    @Operation(summary = "게시판 목록 조회", description = "모든 게시판 목록을 조회합니다.")
    public ApiResponse<List<BoardResponse>> getBoards() {
        List<BoardResponse> response = boardService.getBoards();
        return ApiResponse.success(response);
    }

    /**
     * 그룹별 게시판 목록 조회
     */
    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_READ.value)")
    @Operation(summary = "그룹별 게시판 목록", description = "특정 그룹의 게시판 목록을 조회합니다.")
    public ApiResponse<List<BoardResponse>> getBoardsByGroupId(@PathVariable Long groupId) {
        List<BoardResponse> response = boardService.getBoardsByGroupId(groupId);
        return ApiResponse.success(response);
    }

    /**
     * 게시판 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_READ.value)")
    @Operation(summary = "게시판 상세 조회", description = "게시판 ID로 상세 정보를 조회합니다.")
    public ApiResponse<BoardResponse> getBoard(@PathVariable Long id) {
        BoardResponse response = boardService.getBoard(id);
        return ApiResponse.success(response);
    }

    /**
     * 게시판 코드로 조회
     */
    @GetMapping("/code/{boardCode}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_READ.value)")
    @Operation(summary = "게시판 코드로 조회", description = "게시판 코드로 상세 정보를 조회합니다.")
    public ApiResponse<BoardResponse> getBoardByCode(@PathVariable String boardCode) {
        BoardResponse response = boardService.getBoardByCode(boardCode);
        return ApiResponse.success(response);
    }

    /**
     * 게시판 생성
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_CREATE.value)")
    @Operation(summary = "게시판 생성", description = "새로운 게시판을 생성합니다.")
    public ApiResponse<BoardResponse> createBoard(
            @Valid @RequestBody BoardCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoardResponse response = boardService.createBoard(request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 게시판 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_UPDATE.value)")
    @Operation(summary = "게시판 수정", description = "게시판 정보를 수정합니다.")
    public ApiResponse<BoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoardResponse response = boardService.updateBoard(id, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 게시판 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_DELETE.value)")
    @Operation(summary = "게시판 삭제", description = "게시판을 삭제합니다.")
    public ApiResponse<Void> deleteBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.deleteBoard(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 게시판 코드 중복 확인
     */
    @GetMapping("/check/code")
    @Operation(summary = "게시판 코드 중복 확인", description = "게시판 코드 중복 여부를 확인합니다.")
    public ApiResponse<Boolean> checkBoardCode(@RequestParam String boardCode) {
        boolean isDuplicated = boardService.isBoardCodeDuplicated(boardCode);
        return ApiResponse.success(isDuplicated);
    }

    /**
     * 사용 가능한 게시판 템플릿 코드 목록 조회
     * templates/site/board/ 하위 폴더명을 템플릿 코드로 사용 (board-list.html, board-post.html, post-form.html)
     */
    @GetMapping("/templates")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).BOARD_READ.value)")
    @Operation(summary = "게시판 템플릿 목록 조회", description = "templates/board/ 폴더의 board-*.html 파일 목록을 반환합니다.")
    public ApiResponse<List<String>> getTemplateCodes() {
        List<String> codes = boardService.getAvailableTemplateCodes();
        return ApiResponse.success(codes);
    }
}
