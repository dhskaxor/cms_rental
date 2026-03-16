package com.nt.cms.menu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.menu.dto.SitePageCreateRequest;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.dto.SitePageUpdateRequest;
import com.nt.cms.menu.mapper.SitePageMapper;
import com.nt.cms.menu.vo.SitePageVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * DefaultSitePageService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class DefaultSitePageServiceTest {

    @Mock
    private SitePageMapper sitePageMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultSitePageService sitePageService;

    @Test
    @DisplayName("전체 페이지 목록 조회 성공")
    void findAll_success() {
        // given
        SitePageVO page1 = createPageVO(1L, "about", "회사소개");
        SitePageVO page2 = createPageVO(2L, "contact", "연락처");
        when(sitePageMapper.findAll()).thenReturn(Arrays.asList(page1, page2));

        // when
        List<SitePageResponse> result = sitePageService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPageCode()).isEqualTo("about");
        assertThat(result.get(1).getPageCode()).isEqualTo("contact");
        verify(sitePageMapper).findAll();
    }

    @Test
    @DisplayName("게시된 페이지 목록 조회 성공")
    void findPublished_success() {
        // given
        SitePageVO page = createPageVO(1L, "about", "회사소개");
        page.setIsPublished(true);
        when(sitePageMapper.findPublished()).thenReturn(List.of(page));

        // when
        List<SitePageResponse> result = sitePageService.findPublished();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsPublished()).isTrue();
    }

    @Test
    @DisplayName("ID로 페이지 조회 성공")
    void findById_success() {
        // given
        SitePageVO page = createPageVO(1L, "about", "회사소개");
        when(sitePageMapper.findById(1L)).thenReturn(page);

        // when
        SitePageResponse result = sitePageService.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPageCode()).isEqualTo("about");
        assertThat(result.getPageTitle()).isEqualTo("회사소개");
    }

    @Test
    @DisplayName("존재하지 않는 페이지 조회 시 예외 발생")
    void findById_notFound() {
        // given
        when(sitePageMapper.findById(999L)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> sitePageService.findById(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("페이지 코드로 조회 성공 (is_published=true 페이지만 Mapper에서 반환)")
    void findByPageCode_success() {
        // given: Mapper는 is_published=true 조건으로만 조회하여 공개 페이지만 반환
        SitePageVO page = createPageVO(1L, "about", "회사소개");
        page.setIsPublished(true);
        when(sitePageMapper.findByPageCode("about")).thenReturn(page);

        // when
        SitePageResponse result = sitePageService.findByPageCode("about");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPageCode()).isEqualTo("about");
    }

    @Test
    @DisplayName("페이지 코드로 조회 - 미게시 페이지는 PAGE_NOT_FOUND")
    void findByPageCode_unpublished_returnsNotFound() {
        // given: Mapper에서 is_published=true 조건으로 조회하므로 미게시 페이지는 null 반환
        when(sitePageMapper.findByPageCode("draft")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> sitePageService.findByPageCode("draft"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("페이지 생성 성공")
    void create_success() {
        // given
        SitePageCreateRequest request = SitePageCreateRequest.builder()
                .pageCode("about")
                .pageTitle("회사소개")
                .content("<h1>회사소개</h1>")
                .isPublished(true)
                .build();

        when(sitePageMapper.countByPageCode(anyString(), any())).thenReturn(0);
        doAnswer(invocation -> {
            SitePageVO page = invocation.getArgument(0);
            page.setId(1L);
            return null;
        }).when(sitePageMapper).insert(any(SitePageVO.class));
        
        SitePageVO savedPage = createPageVO(1L, "about", "회사소개");
        when(sitePageMapper.findById(1L)).thenReturn(savedPage);

        // when
        SitePageResponse result = sitePageService.create(request, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPageCode()).isEqualTo("about");
        verify(sitePageMapper).insert(any(SitePageVO.class));
    }

    @Test
    @DisplayName("페이지 생성 실패 - 중복 코드")
    void create_duplicateCode() {
        // given
        SitePageCreateRequest request = SitePageCreateRequest.builder()
                .pageCode("about")
                .pageTitle("회사소개")
                .build();

        when(sitePageMapper.countByPageCode("about", null)).thenReturn(1);

        // when & then
        assertThatThrownBy(() -> sitePageService.create(request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAGE_CODE_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("페이지 수정 성공")
    void update_success() {
        // given
        SitePageVO existing = createPageVO(1L, "about", "회사소개");
        
        SitePageUpdateRequest request = SitePageUpdateRequest.builder()
                .pageTitle("회사소개 - 수정")
                .content("<h1>수정된 내용</h1>")
                .isPublished(false)
                .build();

        when(sitePageMapper.findById(1L)).thenReturn(existing);
        doNothing().when(sitePageMapper).update(any(SitePageVO.class));

        // when
        SitePageResponse result = sitePageService.update(1L, request, 1L);

        // then
        assertThat(result).isNotNull();
        verify(sitePageMapper).update(any(SitePageVO.class));
    }

    @Test
    @DisplayName("페이지 수정 실패 - 존재하지 않는 페이지")
    void update_notFound() {
        // given
        when(sitePageMapper.findById(999L)).thenReturn(null);

        SitePageUpdateRequest request = SitePageUpdateRequest.builder()
                .pageTitle("수정")
                .build();

        // when & then
        assertThatThrownBy(() -> sitePageService.update(999L, request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAGE_NOT_FOUND);
    }

    @Test
    @DisplayName("페이지 삭제 성공")
    void delete_success() {
        // given
        SitePageVO page = createPageVO(1L, "about", "회사소개");
        when(sitePageMapper.findById(1L)).thenReturn(page);
        when(sitePageMapper.countMenuReferences(1L)).thenReturn(0);
        doNothing().when(sitePageMapper).delete(anyLong(), anyLong());

        // when
        sitePageService.delete(1L, 1L);

        // then
        verify(sitePageMapper).delete(1L, 1L);
    }

    @Test
    @DisplayName("페이지 삭제 실패 - 메뉴에서 사용 중")
    void delete_inUse() {
        // given
        SitePageVO page = createPageVO(1L, "about", "회사소개");
        when(sitePageMapper.findById(1L)).thenReturn(page);
        when(sitePageMapper.countMenuReferences(1L)).thenReturn(2);

        // when & then
        assertThatThrownBy(() -> sitePageService.delete(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAGE_IN_USE);
    }

    @Test
    @DisplayName("페이지 코드 중복 체크")
    void existsByPageCode() {
        // given
        when(sitePageMapper.countByPageCode("about", null)).thenReturn(1);
        when(sitePageMapper.countByPageCode("new-page", null)).thenReturn(0);

        // when & then
        assertThat(sitePageService.existsByPageCode("about", null)).isTrue();
        assertThat(sitePageService.existsByPageCode("new-page", null)).isFalse();
    }

    /**
     * 테스트용 SitePageVO 생성
     */
    private SitePageVO createPageVO(Long id, String pageCode, String pageTitle) {
        return SitePageVO.builder()
                .id(id)
                .pageCode(pageCode)
                .pageTitle(pageTitle)
                .content("<h1>" + pageTitle + "</h1>")
                .isPublished(true)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .deleted(false)
                .build();
    }
}
