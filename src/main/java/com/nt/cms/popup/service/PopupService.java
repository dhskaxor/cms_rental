package com.nt.cms.popup.service;

import com.nt.cms.common.response.PageResponse;
import com.nt.cms.popup.dto.PopupCreateRequest;
import com.nt.cms.popup.dto.PopupResponse;
import com.nt.cms.popup.dto.PopupSearchRequest;
import com.nt.cms.popup.dto.PopupUpdateRequest;

import java.util.List;

/**
 * 팝업 서비스 인터페이스
 *
 * @author CMS Team
 */
public interface PopupService {

    PageResponse<PopupResponse> getPopups(PopupSearchRequest request);
    PopupResponse getPopup(Long id);
    PopupResponse getPopupByCode(String popupCode);
    List<PopupResponse> getDisplayPopups(String positionType, String deviceType, boolean isLogin);
    PopupResponse createPopup(PopupCreateRequest request, Long createdBy);
    PopupResponse updatePopup(Long id, PopupUpdateRequest request, Long updatedBy);
    void deletePopup(Long id, Long deletedBy);
    boolean isPopupCodeDuplicated(String popupCode);
}
