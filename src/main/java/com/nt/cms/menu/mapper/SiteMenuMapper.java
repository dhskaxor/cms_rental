package com.nt.cms.menu.mapper;

import com.nt.cms.menu.vo.SiteMenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 사이트 메뉴 Mapper 인터페이스
 * 
 * @author CMS Team
 */
@Mapper
public interface SiteMenuMapper {

    /**
     * 전체 메뉴 목록 조회 (계층 구조 없이 flat)
     *
     * @return 메뉴 목록
     */
    List<SiteMenuVO> findAll();

    /**
     * 최상위 메뉴 목록 조회 (parentId가 NULL인 메뉴)
     *
     * @return 최상위 메뉴 목록
     */
    List<SiteMenuVO> findRootMenus();

    /**
     * 특정 부모의 하위 메뉴 목록 조회
     *
     * @param parentId 부모 메뉴 ID
     * @return 하위 메뉴 목록
     */
    List<SiteMenuVO> findByParentId(@Param("parentId") Long parentId);

    /**
     * ID로 메뉴 조회
     *
     * @param id 메뉴 ID
     * @return 메뉴 정보
     */
    SiteMenuVO findById(@Param("id") Long id);

    /**
     * 메뉴 코드로 조회
     *
     * @param menuCode 메뉴 코드
     * @return 메뉴 정보
     */
    SiteMenuVO findByMenuCode(@Param("menuCode") String menuCode);

    /**
     * 메뉴 코드 중복 체크
     *
     * @param menuCode 메뉴 코드
     * @param excludeId 제외할 ID (수정 시)
     * @return 중복 개수
     */
    int countByMenuCode(@Param("menuCode") String menuCode, @Param("excludeId") Long excludeId);

    /**
     * 하위 메뉴 개수 조회
     *
     * @param parentId 부모 메뉴 ID
     * @return 하위 메뉴 개수
     */
    int countChildren(@Param("parentId") Long parentId);

    /**
     * 메뉴 생성
     *
     * @param menu 메뉴 정보
     */
    void insert(SiteMenuVO menu);

    /**
     * 메뉴 수정
     *
     * @param menu 메뉴 정보
     */
    void update(SiteMenuVO menu);

    /**
     * 메뉴 삭제 (Soft Delete)
     *
     * @param id 메뉴 ID
     * @param updatedBy 수정자 ID
     */
    void delete(@Param("id") Long id, @Param("updatedBy") Long updatedBy);

    /**
     * 정렬 순서 업데이트
     *
     * @param id 메뉴 ID
     * @param sortOrder 정렬 순서
     */
    void updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 노출 가능한 메뉴 목록 조회 (현재 시간 기준)
     *
     * @param isLoginRequired 로그인 필요 여부 (null이면 전체)
     * @return 노출 가능한 메뉴 목록
     */
    List<SiteMenuVO> findVisibleMenus(@Param("isLoginRequired") Boolean isLoginRequired);
}
