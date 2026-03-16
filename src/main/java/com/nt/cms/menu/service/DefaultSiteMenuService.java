package com.nt.cms.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.menu.dto.SiteMenuCreateRequest;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SiteMenuUpdateRequest;
import com.nt.cms.menu.mapper.SiteMenuMapper;
import com.nt.cms.menu.vo.SiteMenuVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 사이트 메뉴 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultSiteMenuService implements SiteMenuService {

    /**
     * 최대 메뉴 깊이 제한
     */
    private static final int MAX_MENU_DEPTH = 3;

    private final SiteMenuMapper siteMenuMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<SiteMenuResponse> findAll() {
        List<SiteMenuVO> menus = siteMenuMapper.findAll();
        return SiteMenuResponse.fromList(menus);
    }

    @Override
    public List<SiteMenuResponse> findListForAdmin() {
        List<SiteMenuVO> allMenus = siteMenuMapper.findAll();
        List<SiteMenuResponse> hierarchy = buildHierarchy(allMenus);
        return flattenHierarchy(hierarchy);
    }

    /**
     * 계층 구조를 flat 리스트로 변환 (DFS 순서)
     */
    private List<SiteMenuResponse> flattenHierarchy(List<SiteMenuResponse> hierarchy) {
        List<SiteMenuResponse> result = new ArrayList<>();
        flattenHierarchyRecursive(hierarchy, result);
        return result;
    }

    private void flattenHierarchyRecursive(List<SiteMenuResponse> menus, List<SiteMenuResponse> result) {
        if (menus == null) return;
        for (SiteMenuResponse menu : menus) {
            result.add(menu);
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                flattenHierarchyRecursive(menu.getChildren(), result);
            }
        }
    }

    @Override
    public List<SiteMenuResponse> findHierarchy() {
        List<SiteMenuVO> allMenus = siteMenuMapper.findAll();
        return buildHierarchy(allMenus);
    }

    @Override
    public List<SiteMenuResponse> findRootMenus() {
        List<SiteMenuVO> menus = siteMenuMapper.findRootMenus();
        return SiteMenuResponse.fromList(menus);
    }

    @Override
    public List<SiteMenuResponse> findByParentId(Long parentId) {
        List<SiteMenuVO> menus = siteMenuMapper.findByParentId(parentId);
        return SiteMenuResponse.fromList(menus);
    }

    @Override
    public SiteMenuResponse findById(Long id) {
        SiteMenuVO menu = siteMenuMapper.findById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        return SiteMenuResponse.from(menu);
    }

    @Override
    public SiteMenuResponse findByMenuCode(String menuCode) {
        SiteMenuVO menu = siteMenuMapper.findByMenuCode(menuCode);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        return SiteMenuResponse.from(menu);
    }

    @Override
    public boolean existsByMenuCode(String menuCode, Long excludeId) {
        return siteMenuMapper.countByMenuCode(menuCode, excludeId) > 0;
    }

    @Override
    @Transactional
    public SiteMenuResponse create(SiteMenuCreateRequest request, Long userId) {
        // 메뉴 코드 중복 체크
        if (existsByMenuCode(request.getMenuCode(), null)) {
            throw new BusinessException(ErrorCode.MENU_CODE_ALREADY_EXISTS);
        }

        // 깊이 계산
        int depth = 1;
        if (request.getParentId() != null) {
            SiteMenuVO parent = siteMenuMapper.findById(request.getParentId());
            if (parent == null) {
                throw new BusinessException(ErrorCode.MENU_INVALID_PARENT);
            }
            depth = parent.getDepth() + 1;
            
            // 최대 깊이 체크
            if (depth > MAX_MENU_DEPTH) {
                throw new BusinessException(ErrorCode.MENU_DEPTH_EXCEEDED);
            }
        }

        SiteMenuVO menu = SiteMenuVO.builder()
                .parentId(request.getParentId())
                .depth(depth)
                .menuName(request.getMenuName())
                .menuCode(request.getMenuCode())
                .menuType(request.getMenuType())
                .urlPath(request.getUrlPath())
                .linkUrl(request.getLinkUrl())
                .boardId(request.getBoardId())
                .pageId(request.getPageId())
                .icon(request.getIcon())
                .target(request.getTarget() != null ? request.getTarget() : "_self")
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isVisible(request.getIsVisible() != null ? request.getIsVisible() : true)
                .isLoginRequired(request.getIsLoginRequired() != null ? request.getIsLoginRequired() : false)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .createdAt(LocalDateTime.now())
                .createdBy(userId)
                .build();

        siteMenuMapper.insert(menu);
        log.info("메뉴 생성 완료: id={}, menuCode={}", menu.getId(), menu.getMenuCode());

        recordAuditLog("CREATE", "MENU", menu.getId(), null, menu, userId);

        return findById(menu.getId());
    }

    @Override
    @Transactional
    public SiteMenuResponse update(Long id, SiteMenuUpdateRequest request, Long userId) {
        SiteMenuVO existing = siteMenuMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }

        // 깊이 계산 (부모가 변경된 경우)
        int depth = existing.getDepth();
        if (request.getParentId() != null && !request.getParentId().equals(existing.getParentId())) {
            // 자기 자신을 부모로 설정 불가
            if (request.getParentId().equals(id)) {
                throw new BusinessException(ErrorCode.MENU_INVALID_PARENT);
            }
            
            SiteMenuVO newParent = siteMenuMapper.findById(request.getParentId());
            if (newParent == null) {
                throw new BusinessException(ErrorCode.MENU_INVALID_PARENT);
            }
            depth = newParent.getDepth() + 1;
            
            // 최대 깊이 체크
            if (depth > MAX_MENU_DEPTH) {
                throw new BusinessException(ErrorCode.MENU_DEPTH_EXCEEDED);
            }
        } else if (request.getParentId() == null && existing.getParentId() != null) {
            // 최상위로 변경
            depth = 1;
        }

        existing.setParentId(request.getParentId());
        existing.setDepth(depth);
        
        if (request.getMenuName() != null) {
            existing.setMenuName(request.getMenuName());
        }
        if (request.getMenuType() != null) {
            existing.setMenuType(request.getMenuType());
        }
        if (request.getUrlPath() != null) {
            existing.setUrlPath(request.getUrlPath());
        }
        if (request.getLinkUrl() != null) {
            existing.setLinkUrl(request.getLinkUrl());
        }
        existing.setBoardId(request.getBoardId());
        existing.setPageId(request.getPageId());
        if (request.getIcon() != null) {
            existing.setIcon(request.getIcon());
        }
        if (request.getTarget() != null) {
            existing.setTarget(request.getTarget());
        }
        if (request.getSortOrder() != null) {
            existing.setSortOrder(request.getSortOrder());
        }
        if (request.getIsVisible() != null) {
            existing.setIsVisible(request.getIsVisible());
        }
        if (request.getIsLoginRequired() != null) {
            existing.setIsLoginRequired(request.getIsLoginRequired());
        }
        existing.setStartAt(request.getStartAt());
        existing.setEndAt(request.getEndAt());
        if (request.getSeoTitle() != null) {
            existing.setSeoTitle(request.getSeoTitle());
        }
        if (request.getSeoDescription() != null) {
            existing.setSeoDescription(request.getSeoDescription());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(userId);

        siteMenuMapper.update(existing);
        log.info("메뉴 수정 완료: id={}", id);

        SiteMenuVO afterMenu = siteMenuMapper.findById(id);
        recordAuditLog("UPDATE", "MENU", id, existing, afterMenu, userId);

        return findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        SiteMenuVO menu = siteMenuMapper.findById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }

        // 하위 메뉴 존재 체크
        int childCount = siteMenuMapper.countChildren(id);
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.MENU_HAS_CHILDREN);
        }

        siteMenuMapper.delete(id, userId);
        log.info("메뉴 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "MENU", id, menu, null, userId);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               SiteMenuVO before, SiteMenuVO after, Long actorId) {
        try {
            String username = userMapper.findUsernameById(actorId);
            String beforeJson = before != null ? objectMapper.writeValueAsString(before) : null;
            String afterJson = after != null ? objectMapper.writeValueAsString(after) : null;
            auditLogService.log(actorId, username, action, targetType, targetId, beforeJson, afterJson);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateSortOrder(Long id, Integer sortOrder) {
        SiteMenuVO menu = siteMenuMapper.findById(id);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        
        siteMenuMapper.updateSortOrder(id, sortOrder);
        log.info("메뉴 정렬 순서 변경: id={}, sortOrder={}", id, sortOrder);
    }

    @Override
    public List<Long> findDescendantIds(Long menuId) {
        if (menuId == null) {
            return List.of();
        }
        List<SiteMenuVO> allMenus = siteMenuMapper.findAll();
        Map<Long, List<SiteMenuVO>> childrenMap = allMenus.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(SiteMenuVO::getParentId));
        List<Long> result = new ArrayList<>();
        collectDescendantIds(menuId, childrenMap, result);
        return result;
    }

    /**
     * 재귀적으로 자손 ID 수집
     */
    private void collectDescendantIds(Long parentId, Map<Long, List<SiteMenuVO>> childrenMap, List<Long> result) {
        List<SiteMenuVO> children = childrenMap.get(parentId);
        if (children == null) return;
        for (SiteMenuVO child : children) {
            result.add(child.getId());
            collectDescendantIds(child.getId(), childrenMap, result);
        }
    }

    @Override
    public List<SiteMenuResponse> findVisibleMenus(boolean includeLoginRequired) {
        Boolean isLoginRequired = includeLoginRequired ? null : false;
        List<SiteMenuVO> menus = siteMenuMapper.findVisibleMenus(isLoginRequired);
        return buildHierarchy(menus);
    }

    /**
     * 메뉴 리스트를 계층 구조로 변환
     *
     * @param menus flat 메뉴 리스트
     * @return 계층형 메뉴 리스트
     */
    private List<SiteMenuResponse> buildHierarchy(List<SiteMenuVO> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // ID를 키로 하는 Map 생성
        Map<Long, SiteMenuVO> menuMap = menus.stream()
                .collect(Collectors.toMap(SiteMenuVO::getId, menu -> {
                    menu.setChildren(new ArrayList<>());
                    return menu;
                }));

        List<SiteMenuVO> rootMenus = new ArrayList<>();

        for (SiteMenuVO menu : menus) {
            if (menu.getParentId() == null) {
                rootMenus.add(menu);
            } else {
                SiteMenuVO parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        return SiteMenuResponse.fromList(rootMenus);
    }
}
