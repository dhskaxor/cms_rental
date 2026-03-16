package com.nt.cms.popup.dto;

import com.nt.cms.popup.vo.SitePopupVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 팝업 응답 DTO
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupResponse {

    private Long id;
    private String popupCode;
    private String popupName;
    private String popupType;
    private String positionType;
    private String deviceType;
    private Integer width;
    private Integer height;
    private Integer posX;
    private Integer posY;
    private String content;
    private String linkUrl;
    private String linkTarget;
    private Boolean isLoginRequired;
    private Boolean isTodayCloseEnabled;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer sortOrder;
    private Boolean isActive;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PopupResponse from(SitePopupVO vo) {
        if (vo == null) return null;
        return PopupResponse.builder()
                .id(vo.getId())
                .popupCode(vo.getPopupCode())
                .popupName(vo.getPopupName())
                .popupType(vo.getPopupType() != null ? vo.getPopupType() : "LAYER")
                .positionType(vo.getPositionType() != null ? vo.getPositionType() : "MAIN")
                .deviceType(vo.getDeviceType() != null ? vo.getDeviceType() : "ALL")
                .width(vo.getWidth())
                .height(vo.getHeight())
                .posX(vo.getPosX() != null ? vo.getPosX() : 0)
                .posY(vo.getPosY() != null ? vo.getPosY() : 0)
                .content(vo.getContent())
                .linkUrl(vo.getLinkUrl())
                .linkTarget(vo.getLinkTarget() != null ? vo.getLinkTarget() : "_self")
                .isLoginRequired(Boolean.TRUE.equals(vo.getIsLoginRequired()))
                .isTodayCloseEnabled(vo.getIsTodayCloseEnabled() == null || Boolean.TRUE.equals(vo.getIsTodayCloseEnabled()))
                .startAt(vo.getStartAt())
                .endAt(vo.getEndAt())
                .sortOrder(vo.getSortOrder() != null ? vo.getSortOrder() : 0)
                .isActive(vo.getIsActive() == null || Boolean.TRUE.equals(vo.getIsActive()))
                .isPublished(vo.getIsPublished() == null || Boolean.TRUE.equals(vo.getIsPublished()))
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }
}
