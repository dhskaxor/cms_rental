package com.nt.cms.popup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 팝업 검색 요청 DTO
 *
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupSearchRequest {

    private String keyword;
    private String popupType;
    private String positionType;
    private String deviceType;
    private Boolean isActive;
    private Boolean isPublished;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 20;

    public int getOffset() {
        return ((page != null ? page : 1) - 1) * (size != null ? size : 20);
    }
}
