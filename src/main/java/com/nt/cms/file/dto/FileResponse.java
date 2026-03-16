package com.nt.cms.file.dto;

import com.nt.cms.file.vo.FileVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 파일 응답 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {

    /**
     * 파일 ID
     */
    private Long id;

    /**
     * 참조 타입
     */
    private String refType;

    /**
     * 참조 ID
     */
    private Long refId;

    /**
     * 원본 파일명
     */
    private String originalName;

    /**
     * 파일 크기 (바이트)
     */
    private Long fileSize;

    /**
     * MIME 타입
     */
    private String mimeType;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 다운로드 URL (API 경로)
     */
    private String downloadUrl;

    /**
     * FileVO로부터 응답 DTO 생성
     * 
     * @param file 파일 VO
     * @return FileResponse
     */
    public static FileResponse from(FileVO file) {
        if (file == null) {
            return null;
        }
        return FileResponse.builder()
                .id(file.getId())
                .refType(file.getRefType())
                .refId(file.getRefId())
                .originalName(file.getOriginalName())
                .fileSize(file.getFileSize())
                .mimeType(file.getMimeType())
                .createdAt(file.getCreatedAt())
                .downloadUrl("/api/v1/files/" + file.getId() + "/download")
                .build();
    }
}
