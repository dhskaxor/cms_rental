package com.nt.cms.popup.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 사이트 팝업 VO
 *
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SitePopupVO extends BaseVO {

    private String popupCode;
    private String popupName;
    private String popupType;      // LAYER, WINDOW, MODAL
    private String positionType;   // MAIN, SUB, ALL
    private String deviceType;     // PC, MOBILE, ALL
    private Integer width;
    private Integer height;
    private Integer posX;
    private Integer posY;
    private String content;
    private String linkUrl;
    private String linkTarget;     // _self, _blank
    private Boolean isLoginRequired;
    private Boolean isTodayCloseEnabled;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer sortOrder;
    private Boolean isActive;
    private Boolean isPublished;
}
