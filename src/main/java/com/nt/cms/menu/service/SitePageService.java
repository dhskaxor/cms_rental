package com.nt.cms.menu.service;

import com.nt.cms.menu.dto.SitePageCreateRequest;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.dto.SitePageUpdateRequest;

import java.util.List;

/**
 * 사이트 페이지 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface SitePageService {

    /**
     * 전체 페이지 목록 조회
     *
     * @return 페이지 목록
     */
    List<SitePageResponse> findAll();

    /**
     * 게시된 페이지 목록 조회
     *
     * @return 게시된 페이지 목록
     */
    List<SitePageResponse> findPublished();

    /**
     * ID로 페이지 조회
     *
     * @param id 페이지 ID
     * @return 페이지 정보
     */
    SitePageResponse findById(Long id);

    /**
     * 페이지 코드로 조회
     *
     * @param pageCode 페이지 코드
     * @return 페이지 정보
     */
    SitePageResponse findByPageCode(String pageCode);

    /**
     * 페이지 코드 중복 체크
     *
     * @param pageCode 페이지 코드
     * @param excludeId 제외할 ID (수정 시)
     * @return 중복 여부
     */
    boolean existsByPageCode(String pageCode, Long excludeId);

    /**
     * 페이지 생성
     *
     * @param request 생성 요청
     * @param userId 생성자 ID
     * @return 생성된 페이지 정보
     */
    SitePageResponse create(SitePageCreateRequest request, Long userId);

    /**
     * 페이지 수정
     *
     * @param id 페이지 ID
     * @param request 수정 요청
     * @param userId 수정자 ID
     * @return 수정된 페이지 정보
     */
    SitePageResponse update(Long id, SitePageUpdateRequest request, Long userId);

    /**
     * 페이지 삭제
     *
     * @param id 페이지 ID
     * @param userId 삭제자 ID
     */
    void delete(Long id, Long userId);

    /**
     * 사용 가능한 페이지 템플릿 코드 목록 조회
     * templates/site/page/ 하위 폴더명을 템플릿 코드로 사용
     *
     * @return 템플릿 코드 목록 (default 포함)
     */
    List<String> getAvailablePageTemplateCodes();

    /**
     * 템플릿 코드에 해당하는 페이지 뷰 이름 해석
     * site/page/{templateCode}/page 존재 시 해당 뷰 반환, 없으면 site/page
     *
     * @param templateCode 템플릿 코드
     * @return Thymeleaf 뷰 이름
     */
    String resolvePageView(String templateCode);
}
