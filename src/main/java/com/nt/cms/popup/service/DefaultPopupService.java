package com.nt.cms.popup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.popup.dto.PopupCreateRequest;
import com.nt.cms.popup.dto.PopupResponse;
import com.nt.cms.popup.dto.PopupSearchRequest;
import com.nt.cms.popup.dto.PopupUpdateRequest;
import com.nt.cms.popup.mapper.PopupMapper;
import com.nt.cms.popup.vo.SitePopupVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 팝업 서비스 구현체
 *
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultPopupService implements PopupService {

    private final PopupMapper popupMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<PopupResponse> getPopups(PopupSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 20;
        PopupSearchRequest searchReq = PopupSearchRequest.builder()
                .keyword(request.getKeyword()).popupType(request.getPopupType())
                .positionType(request.getPositionType()).deviceType(request.getDeviceType())
                .isActive(request.getIsActive()).isPublished(request.getIsPublished())
                .page(page).size(size).build();
        long total = popupMapper.count(searchReq);
        List<SitePopupVO> list = popupMapper.findAll(searchReq);
        List<PopupResponse> content = list.stream().map(PopupResponse::from).collect(Collectors.toList());
        return PageResponse.of(content, page, size, total);
    }

    @Override
    public PopupResponse getPopup(Long id) {
        SitePopupVO vo = popupMapper.findById(id);
        if (vo == null) throw new BusinessException(ErrorCode.POPUP_NOT_FOUND);
        return PopupResponse.from(vo);
    }

    @Override
    public PopupResponse getPopupByCode(String popupCode) {
        SitePopupVO vo = popupMapper.findByPopupCode(popupCode);
        if (vo == null) throw new BusinessException(ErrorCode.POPUP_NOT_FOUND);
        return PopupResponse.from(vo);
    }

    @Override
    public List<PopupResponse> getDisplayPopups(String positionType, String deviceType, boolean isLogin) {
        List<SitePopupVO> list = popupMapper.findDisplayTarget(
                positionType != null ? positionType : "MAIN",
                deviceType != null ? deviceType : "PC",
                isLogin);
        return list.stream().map(PopupResponse::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PopupResponse createPopup(PopupCreateRequest request, Long createdBy) {
        if (popupMapper.existsByPopupCode(request.getPopupCode())) {
            throw new BusinessException(ErrorCode.POPUP_CODE_ALREADY_EXISTS);
        }
        SitePopupVO vo = SitePopupVO.builder()
                .popupCode(request.getPopupCode())
                .popupName(request.getPopupName())
                .popupType(request.getPopupType() != null ? request.getPopupType() : "LAYER")
                .positionType(request.getPositionType() != null ? request.getPositionType() : "MAIN")
                .deviceType(request.getDeviceType() != null ? request.getDeviceType() : "ALL")
                .width(request.getWidth())
                .height(request.getHeight())
                .posX(request.getPosX() != null ? request.getPosX() : 0)
                .posY(request.getPosY() != null ? request.getPosY() : 0)
                .content(request.getContent())
                .linkUrl(request.getLinkUrl())
                .linkTarget(request.getLinkTarget() != null ? request.getLinkTarget() : "_self")
                .isLoginRequired(request.getIsLoginRequired() != null && request.getIsLoginRequired())
                .isTodayCloseEnabled(request.getIsTodayCloseEnabled() == null || request.getIsTodayCloseEnabled())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() == null || request.getIsActive())
                .isPublished(request.getIsPublished() == null || request.getIsPublished())
                .createdBy(createdBy)
                .build();
        vo.onCreate(createdBy);
        popupMapper.insert(vo);
        recordAuditLog("CREATE", "POPUP", vo.getId(), null, vo, createdBy);
        return getPopup(vo.getId());
    }

    @Override
    @Transactional
    public PopupResponse updatePopup(Long id, PopupUpdateRequest request, Long updatedBy) {
        SitePopupVO existing = popupMapper.findById(id);
        if (existing == null) throw new BusinessException(ErrorCode.POPUP_NOT_FOUND);

        SitePopupVO vo = SitePopupVO.builder()
                .id(id)
                .popupName(request.getPopupName())
                .popupType(request.getPopupType())
                .positionType(request.getPositionType())
                .deviceType(request.getDeviceType())
                .width(request.getWidth())
                .height(request.getHeight())
                .posX(request.getPosX())
                .posY(request.getPosY())
                .content(request.getContent())
                .linkUrl(request.getLinkUrl())
                .linkTarget(request.getLinkTarget())
                .isLoginRequired(request.getIsLoginRequired())
                .isTodayCloseEnabled(request.getIsTodayCloseEnabled())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .sortOrder(request.getSortOrder())
                .isActive(request.getIsActive())
                .isPublished(request.getIsPublished())
                .updatedBy(updatedBy)
                .build();
        vo.onUpdate(updatedBy);
        popupMapper.update(vo);
        SitePopupVO afterVo = popupMapper.findById(id);
        recordAuditLog("UPDATE", "POPUP", id, existing, afterVo, updatedBy);
        return getPopup(id);
    }

    @Override
    @Transactional
    public void deletePopup(Long id, Long deletedBy) {
        SitePopupVO existing = popupMapper.findById(id);
        if (existing == null) throw new BusinessException(ErrorCode.POPUP_NOT_FOUND);
        popupMapper.delete(id, deletedBy);
        recordAuditLog("DELETE", "POPUP", id, existing, null, deletedBy);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               SitePopupVO before, SitePopupVO after, Long actorId) {
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
    public boolean isPopupCodeDuplicated(String popupCode) {
        return popupMapper.existsByPopupCode(popupCode);
    }
}
