package com.nt.cms.popup.dto;

import jakarta.validation.constraints.NotBlank;
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
public class PopupCreateRequest {

    @NotBlank(message = "팝업 코드를 입력해주세요.")
    @Size(max = 100)
    private String popupCode;

    @NotBlank(message = "팝업명을 입력해주세요.")
    @Size(max = 200)
    private String popupName;

    private String popupType;      // LAYER, WINDOW, MODAL
    private String positionType;   // MAIN, SUB, ALL
    private String deviceType;     // PC, MOBILE, ALL
    private Integer width;
    private Integer height;
    private Integer posX;
    private Integer posY;
    private String content;
    @Size(max = 500)
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
