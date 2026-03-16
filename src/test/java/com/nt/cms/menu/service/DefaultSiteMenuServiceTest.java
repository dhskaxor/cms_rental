package com.nt.cms.menu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.menu.dto.SiteMenuCreateRequest;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SiteMenuUpdateRequest;
import com.nt.cms.menu.mapper.SiteMenuMapper;
import com.nt.cms.menu.vo.SiteMenuVO;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * DefaultSiteMenuService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class DefaultSiteMenuServiceTest {

    @Mock
    private SiteMenuMapper siteMenuMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultSiteMenuService siteMenuService;

    @Test
    @DisplayName("전체 메뉴 목록 조회 성공")
    void findAll_success() {
        // given
        SiteMenuVO menu1 = createMenuVO(1L, "home", "홈", null, 1);
        SiteMenuVO menu2 = createMenuVO(2L, "about", "소개", null, 1);
        when(siteMenuMapper.findAll()).thenReturn(Arrays.asList(menu1, menu2));

        // when
        List<SiteMenuResponse> result = siteMenuService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMenuCode()).isEqualTo("home");
        assertThat(result.get(1).getMenuCode()).isEqualTo("about");
        verify(siteMenuMapper).findAll();
    }

    @Test
    @DisplayName("ID로 메뉴 조회 성공")
    void findById_success() {
        // given
        SiteMenuVO menu = createMenuVO(1L, "home", "홈", null, 1);
        when(siteMenuMapper.findById(1L)).thenReturn(menu);

        // when
        SiteMenuResponse result = siteMenuService.findById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMenuCode()).isEqualTo("home");
        assertThat(result.getMenuName()).isEqualTo("홈");
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 조회 시 예외 발생")
    void findById_notFound() {
        // given
        when(siteMenuMapper.findById(999L)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> siteMenuService.findById(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MENU_NOT_FOUND);
    }

    @Test
    @DisplayName("메뉴 생성 성공 - 최상위 메뉴")
    void create_rootMenu_success() {
        // given
        SiteMenuCreateRequest request = SiteMenuCreateRequest.builder()
                .menuName("홈")
                .menuCode("home")
                .menuType("PAGE")
                .urlPath("/")
                .sortOrder(0)
                .isVisible(true)
                .build();

        when(siteMenuMapper.countByMenuCode(anyString(), any())).thenReturn(0);
        doAnswer(invocation -> {
            SiteMenuVO menu = invocation.getArgument(0);
            menu.setId(1L);
            return null;
        }).when(siteMenuMapper).insert(any(SiteMenuVO.class));
        
        SiteMenuVO savedMenu = createMenuVO(1L, "home", "홈", null, 1);
        when(siteMenuMapper.findById(1L)).thenReturn(savedMenu);

        // when
        SiteMenuResponse result = siteMenuService.create(request, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMenuCode()).isEqualTo("home");
        assertThat(result.getDepth()).isEqualTo(1);
        verify(siteMenuMapper).insert(any(SiteMenuVO.class));
    }

    @Test
    @DisplayName("메뉴 생성 성공 - 하위 메뉴")
    void create_childMenu_success() {
        // given
        SiteMenuVO parentMenu = createMenuVO(1L, "main", "메인", null, 1);
        
        SiteMenuCreateRequest request = SiteMenuCreateRequest.builder()
                .parentId(1L)
                .menuName("서브메뉴")
                .menuCode("sub")
                .menuType("PAGE")
                .urlPath("/sub")
                .build();

        when(siteMenuMapper.countByMenuCode(anyString(), any())).thenReturn(0);
        when(siteMenuMapper.findById(1L)).thenReturn(parentMenu);
        doAnswer(invocation -> {
            SiteMenuVO menu = invocation.getArgument(0);
            menu.setId(2L);
            return null;
        }).when(siteMenuMapper).insert(any(SiteMenuVO.class));
        
        SiteMenuVO savedMenu = createMenuVO(2L, "sub", "서브메뉴", 1L, 2);
        when(siteMenuMapper.findById(2L)).thenReturn(savedMenu);

        // when
        SiteMenuResponse result = siteMenuService.create(request, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMenuCode()).isEqualTo("sub");
        assertThat(result.getDepth()).isEqualTo(2);
    }

    @Test
    @DisplayName("메뉴 생성 실패 - 중복 코드")
    void create_duplicateCode() {
        // given
        SiteMenuCreateRequest request = SiteMenuCreateRequest.builder()
                .menuName("홈")
                .menuCode("home")
                .menuType("PAGE")
                .build();

        when(siteMenuMapper.countByMenuCode("home", null)).thenReturn(1);

        // when & then
        assertThatThrownBy(() -> siteMenuService.create(request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MENU_CODE_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("메뉴 생성 실패 - 유효하지 않은 상위 메뉴")
    void create_invalidParent() {
        // given
        SiteMenuCreateRequest request = SiteMenuCreateRequest.builder()
                .parentId(999L)
                .menuName("서브")
                .menuCode("sub")
                .menuType("PAGE")
                .build();

        when(siteMenuMapper.countByMenuCode(anyString(), any())).thenReturn(0);
        when(siteMenuMapper.findById(999L)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> siteMenuService.create(request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MENU_INVALID_PARENT);
    }

    @Test
    @DisplayName("메뉴 생성 실패 - 깊이 초과")
    void create_depthExceeded() {
        // given
        SiteMenuVO depth3Menu = createMenuVO(3L, "level3", "3단계", 2L, 3);
        
        SiteMenuCreateRequest request = SiteMenuCreateRequest.builder()
                .parentId(3L)
                .menuName("4단계")
                .menuCode("level4")
                .menuType("PAGE")
                .build();

        when(siteMenuMapper.countByMenuCode(anyString(), any())).thenReturn(0);
        when(siteMenuMapper.findById(3L)).thenReturn(depth3Menu);

        // when & then
        assertThatThrownBy(() -> siteMenuService.create(request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MENU_DEPTH_EXCEEDED);
    }

    @Test
    @DisplayName("메뉴 수정 성공")
    void update_success() {
        // given
        SiteMenuVO existing = createMenuVO(1L, "home", "홈", null, 1);
        
        SiteMenuUpdateRequest request = SiteMenuUpdateRequest.builder()
                .menuName("홈페이지")
                .isVisible(false)
                .build();

        when(siteMenuMapper.findById(1L)).thenReturn(existing);
        doNothing().when(siteMenuMapper).update(any(SiteMenuVO.class));

        // when
        SiteMenuResponse result = siteMenuService.update(1L, request, 1L);

        // then
        assertThat(result).isNotNull();
        verify(siteMenuMapper).update(any(SiteMenuVO.class));
    }

    @Test
    @DisplayName("메뉴 수정 실패 - 존재하지 않는 메뉴")
    void update_notFound() {
        // given
        when(siteMenuMapper.findById(999L)).thenReturn(null);

        SiteMenuUpdateRequest request = SiteMenuUpdateRequest.builder()
                .menuName("수정")
                .build();

        // when & then
        assertThatThrownBy(() -> siteMenuService.update(999L, request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MENU_NOT_FOUND);
    }

    @Test
    @DisplayName("메뉴 삭제 성공")
    void delete_success() {
        // given
        SiteMenuVO menu = createMenuVO(1L, "home", "홈", null, 1);
        when(siteMenuMapper.findById(1L)).thenReturn(menu);
        when(siteMenuMapper.countChildren(1L)).thenReturn(0);
        doNothing().when(siteMenuMapper).delete(anyLong(), anyLong());

        // when
        siteMenuService.delete(1L, 1L);

        // then
        verify(siteMenuMapper).delete(1L, 1L);
    }

    @Test
    @DisplayName("메뉴 삭제 실패 - 하위 메뉴 존재")
    void delete_hasChildren() {
        // given
        SiteMenuVO menu = createMenuVO(1L, "main", "메인", null, 1);
        when(siteMenuMapper.findById(1L)).thenReturn(menu);
        when(siteMenuMapper.countChildren(1L)).thenReturn(2);

        // when & then
        assertThatThrownBy(() -> siteMenuService.delete(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MENU_HAS_CHILDREN);
    }

    @Test
    @DisplayName("메뉴 코드 중복 체크")
    void existsByMenuCode() {
        // given
        when(siteMenuMapper.countByMenuCode("home", null)).thenReturn(1);
        when(siteMenuMapper.countByMenuCode("new-menu", null)).thenReturn(0);

        // when & then
        assertThat(siteMenuService.existsByMenuCode("home", null)).isTrue();
        assertThat(siteMenuService.existsByMenuCode("new-menu", null)).isFalse();
    }

    @Test
    @DisplayName("관리자용 목록 조회 - 계층 순서 flat")
    void findListForAdmin_success() {
        // given: root -> child1 -> grandchild 구조
        SiteMenuVO root = createMenuVO(1L, "main", "메인", null, 1);
        SiteMenuVO child = createMenuVO(2L, "sub1", "서브1", 1L, 2);
        SiteMenuVO grandchild = createMenuVO(3L, "sub1-1", "서브1-1", 2L, 3);
        when(siteMenuMapper.findAll()).thenReturn(Arrays.asList(root, child, grandchild));

        // when
        List<SiteMenuResponse> result = siteMenuService.findListForAdmin();

        // then: DFS 순서로 flat
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getMenuCode()).isEqualTo("main");
        assertThat(result.get(1).getMenuCode()).isEqualTo("sub1");
        assertThat(result.get(2).getMenuCode()).isEqualTo("sub1-1");
    }

    @Test
    @DisplayName("자손 ID 목록 조회 - 순환 참조 방지용")
    void findDescendantIds_success() {
        // given: 1 -> 2 -> 3 구조
        SiteMenuVO root = createMenuVO(1L, "main", "메인", null, 1);
        SiteMenuVO child = createMenuVO(2L, "sub", "서브", 1L, 2);
        SiteMenuVO grandchild = createMenuVO(3L, "sub2", "서브2", 2L, 3);
        when(siteMenuMapper.findAll()).thenReturn(Arrays.asList(root, child, grandchild));

        // when
        List<Long> result = siteMenuService.findDescendantIds(1L);

        // then: 본인 제외, 2, 3 반환 (순서는 부모-자식)
        assertThat(result).hasSize(2);
        assertThat(result).contains(2L, 3L);
    }

    @Test
    @DisplayName("자손 ID 목록 조회 - 자손 없음")
    void findDescendantIds_noChildren() {
        // given
        SiteMenuVO leaf = createMenuVO(1L, "leaf", "단말", null, 1);
        when(siteMenuMapper.findAll()).thenReturn(Arrays.asList(leaf));

        // when
        List<Long> result = siteMenuService.findDescendantIds(1L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("계층형 메뉴 조회")
    void findHierarchy_success() {
        // given
        SiteMenuVO root1 = createMenuVO(1L, "home", "홈", null, 1);
        SiteMenuVO root2 = createMenuVO(2L, "about", "소개", null, 1);
        SiteMenuVO child = createMenuVO(3L, "company", "회사소개", 2L, 2);
        
        when(siteMenuMapper.findAll()).thenReturn(Arrays.asList(root1, root2, child));

        // when
        List<SiteMenuResponse> result = siteMenuService.findHierarchy();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(1).getChildren()).hasSize(1);
        assertThat(result.get(1).getChildren().get(0).getMenuCode()).isEqualTo("company");
    }

    /**
     * 테스트용 SiteMenuVO 생성
     */
    private SiteMenuVO createMenuVO(Long id, String menuCode, String menuName, Long parentId, int depth) {
        return SiteMenuVO.builder()
                .id(id)
                .menuCode(menuCode)
                .menuName(menuName)
                .menuType("PAGE")
                .parentId(parentId)
                .depth(depth)
                .urlPath("/" + menuCode)
                .sortOrder(0)
                .isVisible(true)
                .isLoginRequired(false)
                .target("_self")
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .deleted(false)
                .build();
    }
}
