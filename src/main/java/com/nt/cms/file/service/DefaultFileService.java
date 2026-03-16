package com.nt.cms.file.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.config.CmsProperties;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.dto.FileSearchRequest;
import com.nt.cms.file.mapper.FileMapper;
import com.nt.cms.file.vo.FileVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 파일 관리 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultFileService implements FileService {

    private final FileMapper fileMapper;
    private final CmsProperties cmsProperties;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public FileResponse uploadFile(MultipartFile file, String refType, Long refId) {
        log.debug("파일 업로드: refType={}, refId={}, originalFilename={}", refType, refId, file.getOriginalFilename());

        validateFile(file, 1, 1);

        Path physicalPath = saveToStorage(file, refType, refId);
        FileVO fileVO = saveToDatabase(file, refType, refId, physicalPath);

        return FileResponse.from(fileVO);
    }

    @Override
    @Transactional
    public List<FileResponse> uploadFiles(List<MultipartFile> files, String refType, Long refId, int maxCount) {
        log.debug("다중 파일 업로드: refType={}, refId={}, count={}", refType, refId, files != null ? files.size() : 0);

        if (files == null || files.isEmpty()) {
            return List.of();
        }

        validateFileCount(files, maxCount);

        List<FileResponse> results = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                validateFile(file, files.size(), maxCount);
                Path physicalPath = saveToStorage(file, refType, refId);
                FileVO fileVO = saveToDatabase(file, refType, refId, physicalPath);
                results.add(FileResponse.from(fileVO));
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("파일 업로드 실패: {}", file.getOriginalFilename(), e);
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        return results;
    }

    @Override
    public FileDownloadResult downloadFile(Long id) {
        log.debug("파일 다운로드: id={}", id);

        FileVO fileVO = fileMapper.findById(id);
        if (fileVO == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        Path fullPath = Paths.get(cmsProperties.getFile().getUploadPath(), fileVO.getFilePath());
        if (!Files.exists(fullPath)) {
            log.error("물리 파일 없음: {}", fullPath);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        try {
            byte[] bytes = Files.readAllBytes(fullPath);
            return new FileDownloadResult(bytes, fileVO.getOriginalName(), fileVO.getMimeType());
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", fullPath, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "파일 다운로드에 실패했습니다.");
        }
    }

    @Override
    public FileResponse getFileInfo(Long id) {
        FileVO fileVO = fileMapper.findById(id);
        if (fileVO == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        return FileResponse.from(fileVO);
    }

    @Override
    public List<FileResponse> getFilesByRef(String refType, Long refId) {
        List<FileVO> files = fileMapper.findByRef(refType, refId);
        return files.stream()
                .map(FileResponse::from)
                .toList();
    }

    @Override
    public PageResponse<FileResponse> getFilesForAdmin(FileSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 20;
        FileSearchRequest searchReq = FileSearchRequest.builder()
                .refType(request.getRefType())
                .refId(request.getRefId())
                .page(page)
                .size(size)
                .build();
        long total = fileMapper.countWithFilter(searchReq);
        List<FileVO> list = fileMapper.findAllWithFilter(searchReq);
        List<FileResponse> content = list.stream().map(FileResponse::from).toList();
        return PageResponse.of(content, page, size, total);
    }

    @Override
    @Transactional
    public void deleteFile(Long id, Long deletedBy) {
        log.debug("파일 삭제: id={}", id);

        FileVO fileVO = fileMapper.findById(id);
        if (fileVO == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        fileMapper.delete(id);

        recordAuditLog("DELETE", "FILE", id, fileVO, null, deletedBy);

        // 물리 파일 삭제 (실패 시 로그만 기록, DB는 이미 soft delete 완료)
        Path fullPath = Paths.get(cmsProperties.getFile().getUploadPath(), fileVO.getFilePath());
        try {
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                log.debug("물리 파일 삭제 완료: {}", fullPath);
            }
        } catch (IOException e) {
            log.warn("물리 파일 삭제 실패 (DB는 삭제됨): {}", fullPath, e);
        }
    }

    @Override
    @Transactional
    public void updateRefId(Long fileId, Long refId) {
        if (fileId == null || refId == null) {
            return;
        }
        FileVO fileVO = fileMapper.findById(fileId);
        if (fileVO == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        fileMapper.updateRefId(fileId, refId);
        log.debug("파일 ref_id 갱신: fileId={}, refId={}", fileId, refId);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               FileVO before, FileVO after, Long actorId) {
        try {
            String username = userMapper.findUsernameById(actorId);
            String beforeJson = before != null ? objectMapper.writeValueAsString(before) : null;
            String afterJson = after != null ? objectMapper.writeValueAsString(after) : null;
            auditLogService.log(actorId, username, action, targetType, targetId, beforeJson, afterJson);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
        }
    }

    /**
     * 파일 유효성 검증 (크기, 확장자)
     */
    private void validateFile(MultipartFile file, int currentCount, int maxCount) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "업로드할 파일이 없습니다.");
        }

        long maxSize = cmsProperties.getFile().getMaxSize();
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        String extension = getExtension(file.getOriginalFilename());
        if (extension == null || !isAllowedExtension(extension)) {
            throw new BusinessException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }

        if (currentCount > maxCount) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED);
        }
    }

    /**
     * 파일 개수 검증
     */
    private void validateFileCount(List<MultipartFile> files, int maxCount) {
        long nonEmptyCount = files.stream().filter(f -> f != null && !f.isEmpty()).count();
        if (nonEmptyCount > maxCount) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED);
        }
    }

    /**
     * 허용된 확장자인지 확인 (설정값 기반)
     */
    private boolean isAllowedExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String lowerExt = extension.toLowerCase();
        for (String allowed : cmsProperties.getFile().getAllAllowedExtensions()) {
            if (allowed.equalsIgnoreCase(lowerExt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 파일 확장자 추출
     */
    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return null;
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastDot + 1);
    }

    /**
     * 물리 저장소에 파일 저장
     * 
     * @return 저장된 상대 경로 (uploadPath 기준)
     */
    private Path saveToStorage(MultipartFile file, String refType, Long refId) {
        String extension = getExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String relativePath = refType + "/" + refId + "/" + storedName;
        Path fullPath = Paths.get(cmsProperties.getFile().getUploadPath(), relativePath);

        try {
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath.toFile());
            log.debug("파일 저장 완료: {}", fullPath);
            return Paths.get(relativePath);
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", fullPath, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * DB에 파일 메타정보 저장
     */
    private FileVO saveToDatabase(MultipartFile file, String refType, Long refId, Path relativePath) {
        FileVO fileVO = FileVO.builder()
                .refType(refType)
                .refId(refId != null ? refId : 0L)
                .originalName(file.getOriginalFilename())
                .storedName(relativePath.getFileName().toString())
                .filePath(relativePath.toString().replace("\\", "/"))
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .deleted(false)
                .build();

        fileMapper.insert(fileVO);
        return fileVO;
    }
}
