package com.nt.cms.file.controller;

import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.dto.FileSearchRequest;
import com.nt.cms.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.nt.cms.auth.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 파일 관리 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "파일", description = "파일 업로드/다운로드/삭제 API")
public class FileController {

    private final FileService fileService;

    /**
     * 단일 파일 업로드
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).FILE_CREATE.value)")
    @Operation(summary = "파일 업로드", description = "단일 파일을 업로드합니다. refType, refId로 연결 대상 지정.")
    public ApiResponse<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "refType", defaultValue = "GENERAL") String refType,
            @RequestParam(value = "refId", defaultValue = "0") Long refId) {
        FileResponse response = fileService.uploadFile(file, refType, refId);
        return ApiResponse.success(response);
    }

    /**
     * 다중 파일 업로드 (게시글, 장소/룸 사진 등).
     * 같은 이름 "files"로 여러 파일을 보내면 배열로 수신해 List로 변환합니다.
     */
    @PostMapping(value = "/upload/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).FILE_CREATE.value)")
    @Operation(summary = "다중 파일 업로드", description = "여러 파일을 업로드합니다. maxCount로 최대 개수 제한.")
    public ApiResponse<List<FileResponse>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "refType", defaultValue = "POST") String refType,
            @RequestParam(value = "refId", required = false) Long refId,
            @RequestParam(value = "maxCount", defaultValue = "5") int maxCount) {
        List<MultipartFile> fileList = (files != null && files.length > 0)
                ? Arrays.asList(files)
                : Collections.emptyList();
        List<FileResponse> responses = fileService.uploadFiles(fileList, refType, refId != null ? refId : 0L, maxCount);
        return ApiResponse.success(responses);
    }

    /**
     * 파일 다운로드
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).FILE_READ.value)")
    @Operation(summary = "파일 다운로드", description = "파일 ID로 파일을 다운로드합니다.")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
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
     * 파일 메타정보 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).FILE_READ.value)")
    @Operation(summary = "파일 정보 조회", description = "파일 ID로 메타정보를 조회합니다.")
    public ApiResponse<FileResponse> getFileInfo(@PathVariable Long id) {
        FileResponse response = fileService.getFileInfo(id);
        return ApiResponse.success(response);
    }

    /**
     * 참조 대상의 파일 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).FILE_READ.value)")
    @Operation(summary = "파일 목록 조회", description = "refType, refId로 연결된 파일 목록을 조회합니다.")
    public ApiResponse<List<FileResponse>> getFilesByRef(
            @RequestParam("refType") String refType,
            @RequestParam("refId") Long refId) {
        List<FileResponse> responses = fileService.getFilesByRef(refType, refId);
        return ApiResponse.success(responses);
    }

    /**
     * 관리자용 파일 목록 조회 (refType, refId 선택 필터, 페이징)
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).FILE_READ.value)")
    @Operation(summary = "파일 목록 조회 (관리자)", description = "refType, refId 선택 필터로 파일 목록을 페이징 조회합니다.")
    public ApiResponse<PageResponse<FileResponse>> getFileList(
            @RequestParam(required = false) String refType,
            @RequestParam(required = false) Long refId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        FileSearchRequest request = FileSearchRequest.builder()
                .refType(refType)
                .refId(refId)
                .page(page)
                .size(size)
                .build();
        return ApiResponse.success(fileService.getFilesForAdmin(request));
    }

    /**
     * 파일 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).FILE_DELETE.value)")
    @Operation(summary = "파일 삭제", description = "파일을 삭제합니다. (Soft Delete + 물리 파일 삭제)")
    public ApiResponse<Void> deleteFile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        fileService.deleteFile(id, userDetails.getUserId());
        return ApiResponse.success();
    }
}
