package com.nt.cms.publicapi.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.board.dto.PostResponse;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자단 공개 파일 API
 *
 * <p>게시글 첨부파일 목록 조회 및 다운로드를 인증 없이 제공한다.</p>
 * <p>다운로드 시 postId로 소속 검증하여 게시글 첨부파일만 허용한다.</p>
 *
 * @author CMS Team
 */
@Tag(name = "Public File", description = "사용자단 공개 파일 API (인증 불필요)")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicFileController {

    private final FileService fileService;
    private final PostService postService;
    private final BoardService boardService;
    private final RoleMapper roleMapper;

    /**
     * 게시글 첨부파일 목록 조회 (인증 불필요)
     */
    @Operation(summary = "게시글 첨부파일 목록", description = "게시글에 첨부된 파일 목록을 조회합니다.")
    @GetMapping("/boards/{boardId}/posts/{postId}/files")
    public ApiResponse<List<FileResponse>> getPostFiles(
            @PathVariable Long boardId,
            @PathVariable Long postId) {
        List<FileResponse> files = fileService.getFilesByRef("POST", postId);
        List<FileResponse> withPublicUrl = files.stream()
                .map(f -> FileResponse.builder()
                        .id(f.getId())
                        .refType(f.getRefType())
                        .refId(f.getRefId())
                        .originalName(f.getOriginalName())
                        .fileSize(f.getFileSize())
                        .mimeType(f.getMimeType())
                        .createdAt(f.getCreatedAt())
                        .downloadUrl("/api/v1/public/files/" + f.getId() + "/download?postId=" + postId)
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.success(withPublicUrl);
    }

    /**
     * 게시글 본문 이미지 표시 (인증 불필요)
     * ref_type=POST인 파일은 postId 없이 조회 가능 (에디터 인라인 이미지)
     */
    @Operation(summary = "본문 이미지", description = "게시글 본문에 삽입된 이미지를 조회합니다.")
    @GetMapping("/files/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        FileResponse fileInfo = fileService.getFileInfo(id);
        if (fileInfo == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        if (!"POST".equals(fileInfo.getRefType())) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        String mime = fileInfo.getMimeType();
        if (mime == null || !mime.startsWith("image/")) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        FileService.FileDownloadResult result = fileService.downloadFile(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.mimeType()));
        return ResponseEntity.ok().headers(headers).body(result.bytes());
    }

    /**
     * 게시글 첨부파일 다운로드 (인증 불필요, postId로 소속 검증)
     * postId=0이면 에디터 인라인 이미지(refId=0, 작성 전 업로드) 허용.
     */
    @Operation(summary = "첨부파일 다운로드", description = "게시글 첨부파일을 다운로드합니다. postId로 소속 검증. postId=0이면 대기 중인 에디터 이미지.")
    @GetMapping("/files/{id}/download")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long id,
            @RequestParam(value = "postId", required = false) Long postId) {
        FileResponse fileInfo = fileService.getFileInfo(id);
        if (fileInfo == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        if (!"POST".equals(fileInfo.getRefType())) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        Long refId = fileInfo.getRefId() != null ? fileInfo.getRefId() : 0L;
        boolean valid = (postId != null && postId.equals(refId)) || (postId == null && refId == 0L);
        if (!valid) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        FileService.FileDownloadResult result = fileService.downloadFile(id);
        String encodedFilename = URLEncoder.encode(result.originalName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.mimeType()));
        headers.setContentDispositionFormData("attachment", encodedFilename);
        headers.setContentLength(result.bytes().length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(result.bytes());
    }

    /**
     * 에디터 인라인 이미지 업로드 (WYSIWYG 에디터용)
     * 글 작성 전에도 업로드 가능. postId 없으면 refId=0으로 저장 후, 게시글 저장 시 refId 갱신.
     */
    @Operation(summary = "에디터 이미지 업로드", description = "WYSIWYG 에디터 내 이미지 삽입용. boardId 필수, postId는 수정 시에만.")
    @PostMapping(value = "/boards/{boardId}/files/upload/editor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileResponse> uploadEditorImage(
            @PathVariable Long boardId,
            @RequestParam(value = "postId", required = false) Long postId,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        var board = boardService.getBoard(boardId);
        if (board == null) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        String roleCode = (user != null && user.getRoleCode() != null) ? user.getRoleCode() : "ANONYMOUS";
        RoleVO role = roleMapper.findByRoleCode(roleCode);
        Long roleId = role != null ? role.getId() : null;
        boolean needCreate = (postId == null);
        boolean hasPerm = needCreate
                ? boardService.hasPermission(boardId, roleId, "create")
                : boardService.hasPermission(boardId, roleId, "update");
        if (!hasPerm) {
            throw new BusinessException(ErrorCode.BOARD_PERMISSION_DENIED);
        }
        Long refId = (postId != null) ? postId : 0L;
        FileResponse response = fileService.uploadFile(file, "POST", refId);
        FileResponse withUrl = FileResponse.builder()
                .id(response.getId())
                .refType(response.getRefType())
                .refId(response.getRefId())
                .originalName(response.getOriginalName())
                .fileSize(response.getFileSize())
                .mimeType(response.getMimeType())
                .createdAt(response.getCreatedAt())
                .downloadUrl("/api/v1/public/files/" + response.getId() + "/image")
                .build();
        return ApiResponse.success(withUrl);
    }

    /**
     * 게시글 첨부파일 업로드 (사용자 사이트 글쓰기/수정용)
     * 게시글 생성·수정 후 호출하여 파일을 첨부한다.
     */
    @Operation(summary = "게시글 첨부파일 업로드", description = "게시글에 첨부파일을 업로드합니다. board.useFile=true인 게시판에서만 가능합니다.")
    @PostMapping(value = "/boards/{boardId}/posts/{postId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<FileResponse>> uploadPostFiles(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "maxCount", required = false) Integer maxCountParam) {
        PostResponse post = postService.getPost(postId, null, null);
        if (post == null || !boardId.equals(post.getBoardId())) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        var board = boardService.getBoard(boardId);
        if (board == null || !Boolean.TRUE.equals(board.getUseFile())) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }
        int maxCount = maxCountParam != null ? maxCountParam : (board.getMaxFileCount() != null ? board.getMaxFileCount() : 5);
        List<MultipartFile> emptyFiltered = files != null ? new ArrayList<>(files) : new ArrayList<>();
        emptyFiltered.removeIf(f -> f == null || f.isEmpty());
        if (emptyFiltered.isEmpty()) {
            return ApiResponse.success(Collections.emptyList());
        }
        List<FileResponse> responses = fileService.uploadFiles(emptyFiltered, "POST", postId, maxCount);
        return ApiResponse.success(responses);
    }

    /**
     * 게시글 첨부파일 삭제 (사용자 사이트 수정 폼용)
     * 게시판 canUpdate 권한이 있는 사용자만 삭제 가능.
     */
    @Operation(summary = "게시글 첨부파일 삭제", description = "게시글에 첨부된 파일을 삭제합니다. canUpdate 권한 필요.")
    @DeleteMapping("/boards/{boardId}/posts/{postId}/files/{fileId}")
    public ApiResponse<Void> deletePostFile(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long fileId,
            HttpSession session) {
        PostResponse post = postService.getPost(postId, null, null);
        if (post == null || !boardId.equals(post.getBoardId())) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        var board = boardService.getBoard(boardId);
        if (board == null || !Boolean.TRUE.equals(board.getUseFile())) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }
        FileResponse fileInfo = fileService.getFileInfo(fileId);
        if (fileInfo == null || !"POST".equals(fileInfo.getRefType()) || !postId.equals(fileInfo.getRefId())) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        String roleCode = (user != null && user.getRoleCode() != null) ? user.getRoleCode() : "ANONYMOUS";
        RoleVO role = roleMapper.findByRoleCode(roleCode);
        Long roleId = role != null ? role.getId() : null;
        if (roleId == null || !boardService.hasPermission(boardId, roleId, "update")) {
            throw new BusinessException(ErrorCode.BOARD_PERMISSION_DENIED);
        }
        Long deletedBy = user != null ? user.getId() : null;
        fileService.deleteFile(fileId, deletedBy);
        return ApiResponse.success();
    }
}
