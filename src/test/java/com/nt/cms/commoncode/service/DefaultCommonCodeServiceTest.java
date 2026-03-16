package com.nt.cms.commoncode.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.commoncode.dto.CommonCodeCreateRequest;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.commoncode.mapper.CommonCodeGroupMapper;
import com.nt.cms.commoncode.mapper.CommonCodeMapper;
import com.nt.cms.commoncode.vo.CommonCodeGroupVO;
import com.nt.cms.commoncode.vo.CommonCodeVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.*;

/**
 * DefaultCommonCodeService 단위 테스트
 *
 * @author CMS Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultCommonCodeService 테스트")
class DefaultCommonCodeServiceTest {

    @Mock
    private CommonCodeGroupMapper groupMapper;

    @Mock
    private CommonCodeMapper codeMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultCommonCodeService service;

    private CommonCodeGroupVO testGroup;
    private CommonCodeVO testCode;

    @BeforeEach
    void setUp() {
        testGroup = CommonCodeGroupVO.builder()
                .id(1L)
                .groupCode("TEST")
                .groupName("테스트")
                .build();
        testCode = CommonCodeVO.builder()
                .id(1L)
                .groupId(1L)
                .code("CODE01")
                .codeName("코드1")
                .depth(1)
                .sortOrder(0)
                .build();
    }

    @Nested
    @DisplayName("코드 목록 조회")
    class GetCodesTest {

        @Test
        @DisplayName("그룹별 코드 목록 조회 성공")
        void getCodesByGroupId_success() {
            given(groupMapper.findById(1L)).willReturn(testGroup);
            given(codeMapper.findByGroupId(1L)).willReturn(Arrays.asList(testCode));

            var result = service.getCodesByGroupId(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCode()).isEqualTo("CODE01");
        }

        @Test
        @DisplayName("그룹 없을 때 예외")
        void getCodesByGroupId_groupNotFound() {
            given(groupMapper.findById(999L)).willReturn(null);

            assertThatThrownBy(() -> service.getCodesByGroupId(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("코드 상세 조회")
    class GetCodeTest {

        @Test
        @DisplayName("코드 없을 때 예외")
        void getCode_notFound() {
            given(codeMapper.findById(999L)).willReturn(null);

            assertThatThrownBy(() -> service.getCode(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMON_CODE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("코드 생성")
    class CreateCodeTest {

        @Test
        @DisplayName("코드 생성 성공")
        void createCode_success() {
            var request = CommonCodeCreateRequest.builder()
                    .groupId(1L)
                    .code("NEW_CODE")
                    .codeName("새 코드")
                    .build();

            given(groupMapper.findById(1L)).willReturn(testGroup);
            given(codeMapper.existsByGroupIdAndCode(1L, "NEW_CODE", null)).willReturn(false);
            doAnswer(inv -> {
                CommonCodeVO vo = inv.getArgument(0);
                vo.setId(1L);
                return 1;
            }).when(codeMapper).insert(any(CommonCodeVO.class));
            given(codeMapper.findById(1L)).willReturn(testCode);

            var result = service.createCode(request, 1L);

            assertThat(result).isNotNull();
            verify(codeMapper).insert(any(CommonCodeVO.class));
        }

        @Test
        @DisplayName("코드 중복 시 예외")
        void createCode_duplicate() {
            var request = CommonCodeCreateRequest.builder()
                    .groupId(1L)
                    .code("EXISTING")
                    .codeName("기존")
                    .build();

            given(groupMapper.findById(1L)).willReturn(testGroup);
            given(codeMapper.existsByGroupIdAndCode(1L, "EXISTING", null)).willReturn(true);

            assertThatThrownBy(() -> service.createCode(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMMON_CODE_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("활성 코드 조회")
    class GetActiveCodesTest {

        @Test
        @DisplayName("활성 코드 목록 조회")
        void getActiveCodes_success() {
            given(groupMapper.findByGroupCode("TEST")).willReturn(testGroup);
            given(codeMapper.findActiveByGroupCode("TEST")).willReturn(Arrays.asList(testCode));

            var result = service.getActiveCodesByGroupCode("TEST");

            assertThat(result).hasSize(1);
        }
    }
}
