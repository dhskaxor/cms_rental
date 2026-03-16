package com.nt.cms.commoncode.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.commoncode.dto.CommonCodeGroupCreateRequest;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.commoncode.mapper.CommonCodeGroupMapper;
import com.nt.cms.commoncode.mapper.CommonCodeMapper;
import com.nt.cms.commoncode.vo.CommonCodeGroupVO;
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
@DisplayName("DefaultCommonCodeGroupService 테스트")
class DefaultCommonCodeGroupServiceTest {

    @Mock CommonCodeGroupMapper groupMapper;
    @Mock CommonCodeMapper codeMapper;
    @Mock AuditLogService auditLogService;
    @Mock UserMapper userMapper;
    @Mock ObjectMapper objectMapper;
    @InjectMocks DefaultCommonCodeGroupService service;
    private CommonCodeGroupVO testGroup;

    @BeforeEach
    void setUp() {
        testGroup = CommonCodeGroupVO.builder()
                .id(1L).groupCode("TEST_GROUP").groupName("테스트").codeCount(0L).build();
    }

    @Test
    @DisplayName("그룹 목록 조회 성공")
    void getGroups_success() {
        given(groupMapper.findAll()).willReturn(Arrays.asList(testGroup));
        var result = service.getGroups();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupCode()).isEqualTo("TEST_GROUP");
        verify(groupMapper).findAll();
    }

    @Test
    @DisplayName("그룹 조회 없을 때 예외")
    void getGroup_notFound() {
        given(groupMapper.findById(999L)).willReturn(null);
        assertThatThrownBy(() -> service.getGroup(999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
    }

    @Test
    @DisplayName("그룹 코드 중복 시 생성 예외")
    void createGroup_duplicate() {
        var req = CommonCodeGroupCreateRequest.builder().groupCode("EX").groupName("x").build();
        given(groupMapper.existsByGroupCode("EX")).willReturn(true);
        assertThatThrownBy(() -> service.createGroup(req, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMON_CODE_GROUP_ALREADY_EXISTS);
    }
}
