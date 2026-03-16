package com.nt.cms.popup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.popup.dto.PopupCreateRequest;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.popup.dto.PopupSearchRequest;
import com.nt.cms.popup.mapper.PopupMapper;
import com.nt.cms.popup.vo.SitePopupVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultPopupService 테스트")
class DefaultPopupServiceTest {

    @Mock PopupMapper popupMapper;
    @Mock AuditLogService auditLogService;
    @Mock UserMapper userMapper;
    @Mock ObjectMapper objectMapper;
    @InjectMocks DefaultPopupService service;
    private SitePopupVO testPopup;

    @BeforeEach
    void setUp() {
        testPopup = SitePopupVO.builder()
                .id(1L)
                .popupCode("TEST_POPUP")
                .popupName("테스트 팝업")
                .popupType("LAYER")
                .positionType("MAIN")
                .deviceType("ALL")
                .isActive(true)
                .isPublished(true)
                .build();
    }

    @Test
    @DisplayName("팝업 목록 조회 성공")
    void getPopups_success() {
        var request = PopupSearchRequest.builder().page(1).size(10).build();
        given(popupMapper.count(any(PopupSearchRequest.class))).willReturn(1L);
        given(popupMapper.findAll(any(PopupSearchRequest.class))).willReturn(Arrays.asList(testPopup));
        var result = service.getPopups(request);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPopupCode()).isEqualTo("TEST_POPUP");
    }

    @Test
    @DisplayName("팝업 조회 없을 때 예외")
    void getPopup_notFound() {
        given(popupMapper.findById(999L)).willReturn(null);
        assertThatThrownBy(() -> service.getPopup(999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POPUP_NOT_FOUND);
    }

    @Test
    @DisplayName("팝업 코드 중복 시 생성 예외")
    void createPopup_duplicate() {
        var req = PopupCreateRequest.builder().popupCode("EXISTING").popupName("x").build();
        given(popupMapper.existsByPopupCode("EXISTING")).willReturn(true);
        assertThatThrownBy(() -> service.createPopup(req, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POPUP_CODE_ALREADY_EXISTS);
    }
}
