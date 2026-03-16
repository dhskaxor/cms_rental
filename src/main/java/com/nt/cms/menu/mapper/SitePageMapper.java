package com.nt.cms.menu.mapper;

import com.nt.cms.menu.vo.SitePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 사이트 페이지 Mapper 인터페이스
 * 
 * @author CMS Team
 */
@Mapper
public interface SitePageMapper {

    /**
     * 전체 페이지 목록 조회
     *
     * @return 페이지 목록
     */
    List<SitePageVO> findAll();

    /**
     * 게시된 페이지 목록 조회
     *
     * @return 게시된 페이지 목록
     */
    List<SitePageVO> findPublished();

    /**
     * ID로 페이지 조회
     *
     * @param id 페이지 ID
     * @return 페이지 정보
     */
    SitePageVO findById(@Param("id") Long id);

    /**
     * 페이지 코드로 조회
     *
     * @param pageCode 페이지 코드
     * @return 페이지 정보
     */
    SitePageVO findByPageCode(@Param("pageCode") String pageCode);

    /**
     * 페이지 코드 중복 체크
     *
     * @param pageCode 페이지 코드
     * @param excludeId 제외할 ID (수정 시)
     * @return 중복 개수
     */
    int countByPageCode(@Param("pageCode") String pageCode, @Param("excludeId") Long excludeId);

    /**
     * 페이지 생성
     *
     * @param page 페이지 정보
     */
    void insert(SitePageVO page);

    /**
     * 페이지 수정
     *
     * @param page 페이지 정보
     */
    void update(SitePageVO page);

    /**
     * 페이지 삭제 (Soft Delete)
     *
     * @param id 페이지 ID
     * @param updatedBy 수정자 ID
     */
    void delete(@Param("id") Long id, @Param("updatedBy") Long updatedBy);

    /**
     * 메뉴에서 참조 중인 페이지인지 확인
     *
     * @param pageId 페이지 ID
     * @return 참조하는 메뉴 수
     */
    int countMenuReferences(@Param("pageId") Long pageId);
}
