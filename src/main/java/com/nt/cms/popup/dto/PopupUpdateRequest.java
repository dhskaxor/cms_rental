package com.nt.cms.popup.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupUpdateRequest {

    @Size(max = 200)
    private String popupName;

    private String popupType;
    private String positionType;
    private String deviceType;
    private Integer width;
    private Integer height;
    private Integer posX;
    private Integer posY;
    private String content;
    @Size(max = 500)
    private String linkUrl;
    private String linkTarget;
    private Boolean isLoginRequired;
    private Boolean isTodayCloseEnabled;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer sortOrder;
    private Boolean isActive;
    private Boolean isPublished;
}
