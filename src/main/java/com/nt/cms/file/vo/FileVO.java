package com.nt.cms.file.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 파일 VO
 * 
 * <p>file 테이블 매핑 객체. ref_type, ref_id로 게시글, 페이지 등 다양한 엔티티에 다형적 연결.</p>
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {

    /**
     * Primary Key
     */
    private Long id;

    /**
     * 참조 엔티티 타입 (예: POST, PAGE, USER)
     */
    private String refType;

    /**
     * 참조 엔티티 ID
     */
    private Long refId;

    /**
     * 원본 파일명
     */
    private String originalName;

    /**
     * 저장된 파일명 (UUID 기반)
     */
    private String storedName;

    /**
     * 물리적 저장 경로
     */
    private String filePath;

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
     * Soft Delete 플래그
     */
    private Boolean deleted;
}
