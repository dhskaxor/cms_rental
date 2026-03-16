package com.nt.cms.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.menu.dto.SitePageCreateRequest;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.dto.SitePageUpdateRequest;
import com.nt.cms.menu.mapper.SitePageMapper;
import com.nt.cms.menu.vo.SitePageVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사이트 페이지 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultSitePageService implements SitePageService {

    private final SitePageMapper sitePageMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final ResourcePatternResolver resourcePatternResolver;

    @Override
    public List<SitePageResponse> findAll() {
        List<SitePageVO> pages = sitePageMapper.findAll();
        return SitePageResponse.fromList(pages);
    }

    @Override
    public List<SitePageResponse> findPublished() {
        List<SitePageVO> pages = sitePageMapper.findPublished();
        return SitePageResponse.fromList(pages);
    }

    @Override
    public SitePageResponse findById(Long id) {
        SitePageVO page = sitePageMapper.findById(id);
        if (page == null) {
            throw new BusinessException(ErrorCode.PAGE_NOT_FOUND);
        }
        return SitePageResponse.from(page);
    }

    @Override
    public SitePageResponse findByPageCode(String pageCode) {
        SitePageVO page = sitePageMapper.findByPageCode(pageCode);
        if (page == null) {
            throw new BusinessException(ErrorCode.PAGE_NOT_FOUND);
        }
        return SitePageResponse.from(page);
    }

    @Override
    public boolean existsByPageCode(String pageCode, Long excludeId) {
        return sitePageMapper.countByPageCode(pageCode, excludeId) > 0;
    }

    @Override
    @Transactional
    public SitePageResponse create(SitePageCreateRequest request, Long userId) {
        // 페이지 코드 중복 체크
        if (existsByPageCode(request.getPageCode(), null)) {
            throw new BusinessException(ErrorCode.PAGE_CODE_ALREADY_EXISTS);
        }

        String templateCode = (request.getTemplateCode() != null && !request.getTemplateCode().isBlank())
                ? request.getTemplateCode().trim() : "default";

        SitePageVO page = SitePageVO.builder()
                .pageCode(request.getPageCode())
                .pageTitle(request.getPageTitle())
                .content(request.getContent())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : true)
                .templateCode(templateCode)
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .build();

        sitePageMapper.insert(page);
        log.info("페이지 생성 완료: id={}, pageCode={}", page.getId(), page.getPageCode());

        recordAuditLog("CREATE", "PAGE", page.getId(), null, page, userId);

        return findById(page.getId());
    }

    @Override
    @Transactional
    public SitePageResponse update(Long id, SitePageUpdateRequest request, Long userId) {
        SitePageVO existing = sitePageMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.PAGE_NOT_FOUND);
        }

        if (request.getPageTitle() != null) {
            existing.setPageTitle(request.getPageTitle());
        }
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        }
        if (request.getIsPublished() != null) {
            existing.setIsPublished(request.getIsPublished());
        }
        if (request.getTemplateCode() != null) {
            existing.setTemplateCode(request.getTemplateCode().isBlank() ? "default" : request.getTemplateCode().trim());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(userId);

        sitePageMapper.update(existing);
        log.info("페이지 수정 완료: id={}", id);

        SitePageVO afterPage = sitePageMapper.findById(id);
        recordAuditLog("UPDATE", "PAGE", id, existing, afterPage, userId);

        return findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        SitePageVO page = sitePageMapper.findById(id);
        if (page == null) {
            throw new BusinessException(ErrorCode.PAGE_NOT_FOUND);
        }

        // 메뉴에서 참조 중인지 확인
        int referenceCount = sitePageMapper.countMenuReferences(id);
        if (referenceCount > 0) {
            throw new BusinessException(ErrorCode.PAGE_IN_USE);
        }

        sitePageMapper.delete(id, userId);
        log.info("페이지 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "PAGE", id, page, null, userId);
    }

    @Override
    public List<String> getAvailablePageTemplateCodes() {
        try {
            Resource[] resources = resourcePatternResolver.getResources(
                    "classpath*:templates/site/page/*/*.html");
            List<String> codes = new ArrayList<>();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("page/([^/]+)/[^/]+\\.html$");
            for (Resource r : resources) {
                try {
                    String path = r.getURI().toString().replace('\\', '/');
                    java.util.regex.Matcher m = pattern.matcher(path);
                    if (m.find() && !m.group(1).isEmpty()) {
                        String code = m.group(1);
                        if (!codes.contains(code)) {
                            codes.add(code);
                        }
                    }
                } catch (Exception e) {
                    log.debug("리소스 경로 파싱 건너뜀: {}", r);
                }
            }
            codes.sort(String::compareTo);
            return codes.isEmpty() ? List.of("default") : codes;
        } catch (IOException e) {
            log.warn("페이지 템플릿 목록 조회 실패: {}", e.getMessage());
            return List.of("default");
        }
    }

    @Override
    public String resolvePageView(String templateCode) {
        String code = (templateCode != null && !templateCode.isBlank()) ? templateCode.trim() : "default";
        return resolveView("site/page/" + code + "/page", "site/page");
    }

    /** 템플릿 뷰 존재 여부 확인 후 적절한 뷰 이름 반환 */
    private String resolveView(String... candidates) {
        for (String viewName : candidates) {
            if (existsTemplate(viewName)) {
                return viewName;
            }
        }
        return candidates[candidates.length - 1];
    }

    /** Thymeleaf 템플릿 파일 존재 여부 확인 */
    private boolean existsTemplate(String viewName) {
        try {
            String path = "classpath:templates/" + viewName + ".html";
            Resource[] resources = resourcePatternResolver.getResources(path);
            return resources != null && resources.length > 0 && resources[0].exists();
        } catch (IOException e) {
            return false;
        }
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               SitePageVO before, SitePageVO after, Long actorId) {
        try {
            String username = userMapper.findUsernameById(actorId);
            String beforeJson = before != null ? objectMapper.writeValueAsString(before) : null;
            String afterJson = after != null ? objectMapper.writeValueAsString(after) : null;
            auditLogService.log(actorId, username, action, targetType, targetId, beforeJson, afterJson);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
        }
    }
}
