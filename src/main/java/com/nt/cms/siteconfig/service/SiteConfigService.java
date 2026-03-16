package com.nt.cms.siteconfig.service;

import com.nt.cms.siteconfig.dto.SiteConfigResponse;
import com.nt.cms.siteconfig.dto.SiteConfigUpdateRequest;
import com.nt.cms.siteconfig.vo.SiteConfigVO;

/**
 * 사이트 설정 서비스 인터페이스
 *
 * @author CMS Team
 */
public interface SiteConfigService {

    /**
     * 사이트 설정 조회 (없으면 기본값으로 생성 후 반환)
     *
     * @return 사이트 설정 VO (id=1)
     */
    SiteConfigVO getOrCreate();

    /**
     * 공개용 사이트 설정 조회 (favicon_url 등 URL 포함)
     *
     * @return 사이트 설정 응답 DTO
     */
    SiteConfigResponse getForPublic();

    /**
     * 사이트 설정 수정
     *
     * @param request 수정 요청
     * @param userId 수정자 ID
     */
    void update(SiteConfigUpdateRequest request, Long userId);
}
