package com.nt.cms.siteconfig.service;

import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.file.service.FileService;
import com.nt.cms.siteconfig.dto.SiteConfigResponse;
import com.nt.cms.siteconfig.dto.SiteConfigUpdateRequest;
import com.nt.cms.siteconfig.mapper.SiteConfigMapper;
import com.nt.cms.siteconfig.vo.SiteConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사이트 설정 서비스 구현체
 *
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultSiteConfigService implements SiteConfigService {

    private static final Long SITE_CONFIG_ID = 1L;

    private final SiteConfigMapper siteConfigMapper;
    private final FileService fileService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SiteConfigVO getOrCreate() {
        SiteConfigVO vo = siteConfigMapper.findById(SITE_CONFIG_ID);
        if (vo == null) {
            return createDefault();
        }
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public SiteConfigResponse getForPublic() {
        SiteConfigVO vo = getOrCreate();
        String faviconUrl = null;
        if (vo.getFaviconFileId() != null) {
            faviconUrl = "/api/v1/public/site/favicon";
        }
        return SiteConfigResponse.builder()
                .id(vo.getId())
                .siteName(vo.getSiteName() != null ? vo.getSiteName() : "CMS Core")
                .faviconFileId(vo.getFaviconFileId())
                .faviconUrl(faviconUrl)
                .seoTitle(vo.getSeoTitle())
                .seoDescription(vo.getSeoDescription())
                .seoKeywords(vo.getSeoKeywords())
                .companyAddress(vo.getCompanyAddress())
                .companyPhone(vo.getCompanyPhone())
                .adminEmail(vo.getAdminEmail())
                .build();
    }

    @Override
    @Transactional
    public void update(SiteConfigUpdateRequest request, Long userId) {
        SiteConfigVO existing = getOrCreate();
        SiteConfigVO toUpdate = SiteConfigVO.builder()
                .id(SITE_CONFIG_ID)
                .siteName(request.getSiteName() != null ? request.getSiteName() : existing.getSiteName())
                .faviconFileId(request.getFaviconFileId() != null ? request.getFaviconFileId() : existing.getFaviconFileId())
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .seoKeywords(request.getSeoKeywords())
                .companyAddress(request.getCompanyAddress())
                .companyPhone(request.getCompanyPhone())
                .adminEmail(request.getAdminEmail())
                .updatedAt(LocalDateTime.now())
                .updatedBy(userId)
                .build();
        siteConfigMapper.update(toUpdate);
        log.info("사이트 설정 수정 완료: userId={}", userId);
        auditLogService.record(userId, "UPDATE", "SITE_CONFIG", SITE_CONFIG_ID, existing, toUpdate);
    }

    /**
     * 기본 행 생성 (id=1)
     */
    @Transactional
    protected SiteConfigVO createDefault() {
        SiteConfigVO vo = SiteConfigVO.builder()
                .id(SITE_CONFIG_ID)
                .siteName("CMS Core")
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();
        vo.onCreate(null);
        siteConfigMapper.insert(vo);
        log.info("사이트 설정 초기 행 생성: id=1");
        return vo;
    }
}
