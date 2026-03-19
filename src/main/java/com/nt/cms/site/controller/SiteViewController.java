package com.nt.cms.site.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.board.dto.BoardResponse;
import com.nt.cms.board.dto.LatestPostsRequest;
import com.nt.cms.board.dto.PostResponse;
import com.nt.cms.board.dto.PostSearchRequest;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.CommentService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.service.FileService;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.service.SiteMenuService;
import com.nt.cms.menu.service.SitePageService;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 사용자 사이트 뷰 컨트롤러 (Page/Submit 방식)
 *
 * <p>Thymeleaf로 서버 렌더링하여 페이지를 반환한다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/site")
@RequiredArgsConstructor
public class SiteViewController {

    private final BoardService boardService;
    private final PostService postService;
    private final CommentService commentService;
    private final FileService fileService;
    private final SitePageService sitePageService;
    private final SiteMenuService siteMenuService;
    private final RoleMapper roleMapper;

    /**
     * 메인(대문) 페이지
     * 최신글·게시판 링크는 서버 렌더링 (JS 불필요)
     */
    @GetMapping(value = {"", "/"})
    public String index(Model model) {
        var request = new LatestPostsRequest();
        request.setBoardIds(List.of(1L));
        request.setSize(3);
        request.setSortField("createdAt");
        request.setSortOrder("DESC");
        model.addAttribute("latestPosts", postService.getLatestPosts(request));
        model.addAttribute("boardLinks", collectBoardLinks(siteMenuService.findVisibleMenus(true)));
        return "site/index";
    }

    /** 메뉴에서 BOARD 타입 추출 (계층 평탄화) */
    private List<BoardLinkDto> collectBoardLinks(List<SiteMenuResponse> menus) {
        if (menus == null) return List.of();
        var list = new java.util.ArrayList<BoardLinkDto>();
        collectBoardLinksRecursive(menus, list);
        return list;
    }

    private void collectBoardLinksRecursive(List<SiteMenuResponse> menus, java.util.List<BoardLinkDto> out) {
        if (menus == null) return;
        for (var m : menus) {
            if ("BOARD".equals(m.getMenuType()) && (m.getBoardId() != null || m.getUrlPath() != null)) {
                var href = m.getUrlPath() != null && !m.getUrlPath().isBlank()
                        ? m.getUrlPath()
                        : "/site/board/" + (m.getBoardCode() != null ? m.getBoardCode() : m.getBoardId());
                out.add(new BoardLinkDto(href, m.getMenuName() != null ? m.getMenuName() : m.getBoardName() != null ? m.getBoardName() : "게시판"));
            }
            if (m.getChildren() != null && !m.getChildren().isEmpty()) {
                collectBoardLinksRecursive(m.getChildren(), out);
            }
        }
    }

    /** 게시판 바로가기 링크 DTO */
    public record BoardLinkDto(String href, String name) {}

    /**
     * /site/index.html 접근 시 메인으로 리다이렉트 (기존 SPA index.html 대체)
     */
    @GetMapping("/index.html")
    public String indexHtml() {
        return "redirect:/site/";
    }

    /**
     * 도움말 페이지
     */
    @GetMapping("/help")
    public String help() {
        return "site/help";
    }

    /**
     * 예약 달력 (사용자용)
     */
    @GetMapping("/rental")
    public String rentalCalendar() {
        return "site/rental/calendar";
    }

    /**
     * 예약 진행 페이지 (일자 선택 후 시간대 선택)
     */
    @GetMapping("/rental/reserve")
    public String rentalReserve() {
        return "site/rental/reserve";
    }

    /**
     * 내 정보 (프로필/비밀번호/내 예약)
     */
    @GetMapping("/me")
    public String me(HttpSession session, Model model,
                     @RequestParam(value = "redirect", required = false) String redirect) {
        SessionUser user = session != null ? (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER) : null;
        if (user == null) {
            String target = (redirect != null && !redirect.isBlank()) ? redirect : "/site/me";
            return "redirect:/site/auth/login?redirect=" + java.net.URLEncoder.encode(target, java.nio.charset.StandardCharsets.UTF_8);
        }
        model.addAttribute("me", user);
        return "site/me";
    }

    /**
     * 연락처 페이지 (사이트 설정 기반)
     */
    @GetMapping("/contact")
    public String contact() {
        return "site/contact";
    }

    /**
     * 정적 페이지
     * page.templateCode 기준 템플릿 해석: site/page/{code}/page → 없으면 site/page
     */
    @GetMapping("/page/{pageCode}")
    public String page(@PathVariable String pageCode, Model model) {
        SitePageResponse page;
        try {
            page = sitePageService.findByPageCode(pageCode);
        } catch (BusinessException e) {
            if (ErrorCode.PAGE_NOT_FOUND.equals(e.getErrorCode())) {
                model.addAttribute("page", (SitePageResponse) null);
                return sitePageService.resolvePageView("default");
            }
            throw e;
        }
        model.addAttribute("page", page);
        String templateCode = (page.getTemplateCode() != null && !page.getTemplateCode().isBlank())
                ? page.getTemplateCode().trim() : "default";
        return sitePageService.resolvePageView(templateCode);
    }

    /**
     * 게시판 목록
     */
    @GetMapping("/board/{boardCode}")
    public String boardList(
            @PathVariable String boardCode,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchType,
            HttpSession session,
            Model model) {

        BoardResponse board = resolveBoard(boardCode);
        if (board == null) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("message", "게시판을 찾을 수 없습니다.");
            return "site/error";
        }

        String roleCode = resolveRoleCode(session);
        if (!hasPermission(board, roleCode, "canRead")) {
            model.addAttribute("statusCode", 403);
            model.addAttribute("message", "권한이 없습니다.");
            return "site/error";
        }

        PostSearchRequest searchRequest = PostSearchRequest.builder()
                .page(page)
                .size(size)
                .keyword(keyword)
                .searchType(searchType)
                .build();

        var postsResponse = postService.getPosts(board.getId(), searchRequest);
        String boardPath = board.getBoardCode() != null ? board.getBoardCode() : String.valueOf(board.getId());

        model.addAttribute("board", board);
        model.addAttribute("posts", postsResponse.getContent());
        model.addAttribute("totalElements", postsResponse.getTotalElements());
        model.addAttribute("totalPages", postsResponse.getTotalPages());
        model.addAttribute("currentPage", postsResponse.getPage());
        model.addAttribute("pageSize", postsResponse.getSize());
        model.addAttribute("boardPath", boardPath);
        model.addAttribute("canCreate", hasPermission(board, roleCode, "canCreate"));
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        String templateCode = (board.getTemplateCode() != null && !board.getTemplateCode().isBlank())
                ? board.getTemplateCode().trim() : "default";
        model.addAttribute("templateCode", templateCode);
        return boardService.resolveBoardListView(templateCode);
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/board/{boardCode}/post/{postId}")
    public String postDetail(
            @PathVariable String boardCode,
            @PathVariable Long postId,
            HttpSession session,
            Model model) {

        BoardResponse board = resolveBoard(boardCode);
        if (board == null) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("message", "게시판을 찾을 수 없습니다.");
            return "site/error";
        }

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);

        PostResponse post = postService.getPost(postId, userId, roleId);
        if (post == null) {
            model.addAttribute("error", "게시글을 찾을 수 없습니다.");
            return "site/error";
        }

        String roleCode = resolveRoleCode(session);
        String boardPath = board.getBoardCode() != null ? board.getBoardCode() : String.valueOf(board.getId());
        boolean canUpdate = hasPermission(board, roleCode, "canUpdate");
        boolean canDelete = hasPermission(board, roleCode, "canDelete");
        boolean isMine = user != null && user.getId() != null && user.getId().equals(post.getWriterId());

        List<FileResponse> rawFiles = board.getUseFile() != null && board.getUseFile()
                ? fileService.getFilesByRef("POST", postId)
                : List.of();
        List<FileResponse> files = rawFiles.stream()
                .map(f -> FileResponse.builder()
                        .id(f.getId())
                        .refType(f.getRefType())
                        .refId(f.getRefId())
                        .originalName(f.getOriginalName())
                        .fileSize(f.getFileSize())
                        .mimeType(f.getMimeType())
                        .createdAt(f.getCreatedAt())
                        .downloadUrl("/api/v1/public/files/" + f.getId() + "/download?postId=" + postId)
                        .build())
                .toList();
        var comments = board.getUseComment() != null && board.getUseComment()
                ? commentService.getComments(postId)
                : List.<com.nt.cms.board.dto.CommentResponse>of();

        model.addAttribute("board", board);
        model.addAttribute("post", post);
        model.addAttribute("boardPath", boardPath);
        model.addAttribute("listUrl", "/site/board/" + boardPath);
        model.addAttribute("canUpdate", canUpdate && isMine);
        model.addAttribute("canDelete", canDelete && isMine);
        model.addAttribute("files", files);
        model.addAttribute("comments", comments);
        String templateCode = (board.getTemplateCode() != null && !board.getTemplateCode().isBlank())
                ? board.getTemplateCode().trim() : "default";
        return boardService.resolvePostDetailView(templateCode);
    }

    /**
     * 글쓰기 폼
     */
    @GetMapping("/board/{boardCode}/write")
    public String writeForm(
            @PathVariable String boardCode,
            HttpSession session,
            Model model) {

        BoardResponse board = resolveBoard(boardCode);
        if (board == null) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("message", "게시판을 찾을 수 없습니다.");
            return "site/error";
        }

        String roleCode = resolveRoleCode(session);
        if (!hasPermission(board, roleCode, "canCreate")) {
            model.addAttribute("statusCode", 403);
            model.addAttribute("message", "글쓰기 권한이 없습니다.");
            return "site/error";
        }

        String boardPath = board.getBoardCode() != null ? board.getBoardCode() : String.valueOf(board.getId());
        model.addAttribute("board", board);
        model.addAttribute("boardPath", boardPath);
        model.addAttribute("post", null);
        model.addAttribute("listUrl", "/site/board/" + boardPath);
        model.addAttribute("formTitle", "글쓰기");
        model.addAttribute("submitLabel", "등록");
        String templateCode = (board.getTemplateCode() != null && !board.getTemplateCode().isBlank())
                ? board.getTemplateCode().trim() : "default";
        return boardService.resolvePostFormView(templateCode);
    }

    /**
     * 글 수정 폼
     */
    @GetMapping("/board/{boardCode}/post/{postId}/edit")
    public String editForm(
            @PathVariable String boardCode,
            @PathVariable Long postId,
            HttpSession session,
            Model model) {

        BoardResponse board = resolveBoard(boardCode);
        if (board == null) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("message", "게시판을 찾을 수 없습니다.");
            return "site/error";
        }

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);

        PostResponse post = postService.getPost(postId, userId, roleId);
        if (post == null) {
            model.addAttribute("statusCode", 404);
            model.addAttribute("message", "게시글을 찾을 수 없습니다.");
            return "site/error";
        }

        String roleCode = resolveRoleCode(session);
        if (!hasPermission(board, roleCode, "canUpdate")) {
            model.addAttribute("statusCode", 403);
            model.addAttribute("message", "수정 권한이 없습니다.");
            return "site/error";
        }

        boolean isMine = user != null && user.getId() != null && user.getId().equals(post.getWriterId());
        if (!isMine) {
            model.addAttribute("statusCode", 403);
            model.addAttribute("message", "본인 글만 수정할 수 있습니다.");
            return "site/error";
        }

        String boardPath = board.getBoardCode() != null ? board.getBoardCode() : String.valueOf(board.getId());
        model.addAttribute("board", board);
        model.addAttribute("boardPath", boardPath);
        model.addAttribute("post", post);
        model.addAttribute("listUrl", "/site/board/" + boardPath);
        model.addAttribute("formTitle", "글 수정");
        model.addAttribute("submitLabel", "수정");
        String templateCode = (board.getTemplateCode() != null && !board.getTemplateCode().isBlank())
                ? board.getTemplateCode().trim() : "default";
        return boardService.resolvePostFormView(templateCode);
    }

    private BoardResponse resolveBoard(String boardCode) {
        try {
            Long id = Long.parseLong(boardCode);
            return boardService.getBoard(id);
        } catch (NumberFormatException e) {
            return boardService.getBoardByCode(boardCode);
        }
    }

    private String resolveRoleCode(HttpSession session) {
        SessionUser user = session != null ? (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER) : null;
        return (user != null && user.getRoleCode() != null) ? user.getRoleCode() : "ANONYMOUS";
    }

    private Long resolveRoleId(SessionUser user) {
        String roleCode = (user != null && user.getRoleCode() != null) ? user.getRoleCode() : "ANONYMOUS";
        RoleVO role = roleMapper.findByRoleCode(roleCode);
        return role != null ? role.getId() : null;
    }

    private boolean hasPermission(BoardResponse board, String roleCode, String action) {
        if (board.getPermissions() == null) return false;
        return board.getPermissions().stream()
                .filter(p -> roleCode.equals(p.getRoleCode()))
                .findFirst()
                .map(p -> {
                    return switch (action) {
                        case "canCreate" -> Boolean.TRUE.equals(p.getCanCreate());
                        case "canRead" -> Boolean.TRUE.equals(p.getCanRead());
                        case "canUpdate" -> Boolean.TRUE.equals(p.getCanUpdate());
                        case "canDelete" -> Boolean.TRUE.equals(p.getCanDelete());
                        default -> false;
                    };
                })
                .orElse(false);
    }
}
