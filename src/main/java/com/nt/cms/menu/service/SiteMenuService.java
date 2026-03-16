package com.nt.cms.menu.service;

import com.nt.cms.menu.dto.SiteMenuCreateRequest;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SiteMenuUpdateRequest;

import java.util.List;

/**
 * 사이트 메뉴 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface SiteMenuService {

    /**
     * 전체 메뉴 목록 조회 (flat)
     *
     * @return 메뉴 목록
     */
    List<SiteMenuResponse> findAll();

    /**
     * 관리자 목록용 메뉴 조회 (계층 순서 flat)
     *
     * @return 계층 순서대로 정렬된 flat 목록
     */
    List<SiteMenuResponse> findListForAdmin();

    /**
     * 특정 메뉴의 모든 자손 ID 목록 조회 (순환 참조 방지용)
     *
     * @param menuId 메뉴 ID
     * @return 자손 ID 목록 (본인 미포함)
     */
    List<Long> findDescendantIds(Long menuId);

    /**
     * 계층형 메뉴 목록 조회
     *
     * @return 계층형 메뉴 목록 (최상위 메뉴 + children)
     */
    List<SiteMenuResponse> findHierarchy();

    /**
     * 최상위 메뉴 목록 조회
     *
     * @return 최상위 메뉴 목록
     */
    List<SiteMenuResponse> findRootMenus();

    /**
     * 특정 부모의 하위 메뉴 목록 조회
     *
     * @param parentId 부모 메뉴 ID
     * @return 하위 메뉴 목록
     */
    List<SiteMenuResponse> findByParentId(Long parentId);

    /**
     * ID로 메뉴 조회
     *
     * @param id 메뉴 ID
     * @return 메뉴 정보
     */
    SiteMenuResponse findById(Long id);

    /**
     * 메뉴 코드로 조회
     *
     * @param menuCode 메뉴 코드
     * @return 메뉴 정보
     */
    SiteMenuResponse findByMenuCode(String menuCode);

    /**
     * 메뉴 코드 중복 체크
     *
     * @param menuCode 메뉴 코드
     * @param excludeId 제외할 ID (수정 시)
     * @return 중복 여부
     */
    boolean existsByMenuCode(String menuCode, Long excludeId);

    /**
     * 메뉴 생성
     *
     * @param request 생성 요청
     * @param userId 생성자 ID
     * @return 생성된 메뉴 정보
     */
    SiteMenuResponse create(SiteMenuCreateRequest request, Long userId);

    /**
     * 메뉴 수정
     *
     * @param id 메뉴 ID
     * @param request 수정 요청
     * @param userId 수정자 ID
     * @return 수정된 메뉴 정보
     */
    SiteMenuResponse update(Long id, SiteMenuUpdateRequest request, Long userId);

    /**
     * 메뉴 삭제
     *
     * @param id 메뉴 ID
     * @param userId 삭제자 ID
     */
    void delete(Long id, Long userId);

    /**
     * 정렬 순서 업데이트
     *
     * @param id 메뉴 ID
     * @param sortOrder 정렬 순서
     */
    void updateSortOrder(Long id, Integer sortOrder);

    /**
     * 노출 가능한 메뉴 목록 조회
     *
     * @param includeLoginRequired 로그인 필요 메뉴 포함 여부
     * @return 노출 가능한 메뉴 목록
     */
    List<SiteMenuResponse> findVisibleMenus(boolean includeLoginRequired);
}
