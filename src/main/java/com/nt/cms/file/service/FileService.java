package com.nt.cms.file.service;

import com.nt.cms.common.response.PageResponse;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.dto.FileSearchRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 관리 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface FileService {

    /**
     * 파일 업로드
     * 
     * @param file 업로드할 파일
     * @param refType 참조 타입 (예: POST, PAGE)
     * @param refId 참조 ID
     * @return 업로드된 파일 응답
     */
    FileResponse uploadFile(MultipartFile file, String refType, Long refId);

    /**
     * 다중 파일 업로드
     * 
     * @param files 업로드할 파일 목록
     * @param refType 참조 타입
     * @param refId 참조 ID
     * @param maxCount 최대 업로드 개수
     * @return 업로드된 파일 응답 목록
     */
    List<FileResponse> uploadFiles(List<MultipartFile> files, String refType, Long refId, int maxCount);

    /**
     * 파일 다운로드 (바이트 배열 및 메타정보 반환)
     * 
     * @param id 파일 ID
     * @return 파일 다운로드 결과 (바이트, 원본파일명, MIME타입)
     */
    FileDownloadResult downloadFile(Long id);

    /**
     * 파일 메타정보 조회
     * 
     * @param id 파일 ID
     * @return 파일 응답
     */
    FileResponse getFileInfo(Long id);

    /**
     * 참조 대상의 파일 목록 조회
     *
     * @param refType 참조 타입
     * @param refId 참조 ID
     * @return 파일 응답 목록
     */
    List<FileResponse> getFilesByRef(String refType, Long refId);

    /**
     * 관리자용 파일 목록 조회 (refType/refId 필터, 페이징)
     *
     * @param request 검색 조건
     * @return 페이징된 파일 목록
     */
    PageResponse<FileResponse> getFilesForAdmin(FileSearchRequest request);

    /**
     * 파일 삭제 (Soft Delete + 물리 파일 삭제)
     * 
     * @param id 파일 ID
     * @param deletedBy 삭제 수행 사용자 ID
     */
    void deleteFile(Long id, Long deletedBy);

    /**
     * 파일의 ref_id 갱신 (에디터 인라인 이미지 → 게시글 연결용)
     *
     * @param fileId 파일 ID
     * @param refId 갱신할 ref_id (게시글 ID)
     */
    void updateRefId(Long fileId, Long refId);

    /**
     * 파일 다운로드 결과를 담는 내부 클래스
     */
    record FileDownloadResult(byte[] bytes, String originalName, String mimeType) {}
}
