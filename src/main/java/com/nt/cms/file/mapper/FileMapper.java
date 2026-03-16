package com.nt.cms.file.mapper;

import com.nt.cms.file.dto.FileSearchRequest;
import com.nt.cms.file.vo.FileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 파일 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface FileMapper {

    /**
     * ID로 파일 조회
     * 
     * @param id 파일 ID
     * @return 파일 VO (없으면 null)
     */
    FileVO findById(@Param("id") Long id);

    /**
     * ref_type, ref_id로 파일 목록 조회
     * 
     * @param refType 참조 타입
     * @param refId 참조 ID
     * @return 파일 목록
     */
    List<FileVO> findByRef(@Param("refType") String refType, @Param("refId") Long refId);

    /**
     * 파일 등록
     * 
     * @param file 파일 VO
     * @return 등록된 행 수
     */
    int insert(FileVO file);

    /**
     * 파일 삭제 (Soft Delete)
     * 
     * @param id 파일 ID
     * @return 수정된 행 수
     */
    int delete(@Param("id") Long id);

    /**
     * ref_type, ref_id로 파일 개수 조회
     *
     * @param refType 참조 타입
     * @param refId 참조 ID
     * @return 파일 개수
     */
    long countByRef(@Param("refType") String refType, @Param("refId") Long refId);

    /**
     * 관리자용 파일 목록 조회 (refType, refId 선택 필터, 페이징)
     *
     * @param request 검색 조건
     * @return 파일 목록
     */
    List<FileVO> findAllWithFilter(@Param("request") FileSearchRequest request);

    /**
     * 관리자용 파일 개수 조회
     *
     * @param request 검색 조건
     * @return 파일 개수
     */
    long countWithFilter(@Param("request") FileSearchRequest request);

    /**
     * 파일의 ref_id 갱신 (에디터 인라인 이미지 → 게시글 연결용)
     *
     * @param id 파일 ID
     * @param refId 갱신할 ref_id
     * @return 수정된 행 수
     */
    int updateRefId(@Param("id") Long id, @Param("refId") Long refId);
}
