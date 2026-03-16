package com.nt.cms.site.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.board.dto.BoardResponse;
import com.nt.cms.board.dto.PostCreateRequest;
import com.nt.cms.board.dto.PostResponse;
import com.nt.cms.board.dto.PostUpdateRequest;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.file.service.FileService;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 사이트 폼 제출 처리 (Page/Submit 방식)
 *
 * <p>게시글 작성·수정·삭제를 form POST로 처리한다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/site")
@RequiredArgsConstructor
public class SiteFormController {

    private final BoardService boardService;
    private final PostService postService;
    private final FileService fileService;
    private final RoleMapper roleMapper;

    /**
     * 게시글 작성
     */
    @PostMapping("/board/{boardPath}/post")
    public String createPost(
            @PathVariable String boardPath,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(defaultValue = "false") boolean isSecret,
            @RequestParam(required = false) String attachedFileIds,
            @RequestParam(required = false) MultipartFile[] files,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        BoardResponse board = resolveBoard(boardPath);
        if (board == null) {
            redirectAttributes.addFlashAttribute("message", "게시판을 찾을 수 없습니다.");
            return "redirect:/site/";
        }

        String roleCode = resolveRoleCode(session);
        if (!hasPermission(board, roleCode, "canCreate")) {
            redirectAttributes.addFlashAttribute("message", "글쓰기 권한이 없습니다.");
            return "redirect:/site/board/" + boardPath;
        }

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);

        List<Long> attachedIds = parseAttachedFileIds(attachedFileIds);
        PostCreateRequest request = PostCreateRequest.builder()
                .title(title)
                .content(content != null ? content : "")
                .isNotice(false)
                .isSecret(isSecret)
                .attachedFileIds(attachedIds.isEmpty() ? null : attachedIds)
                .build();

        PostResponse created = postService.createPost(board.getId(), request, userId, roleId);
        Long postId = created.getId();

        if (board.getUseFile() != null && board.getUseFile() && files != null && files.length > 0) {
            int maxCount = board.getMaxFileCount() != null ? board.getMaxFileCount() : 5;
            List<MultipartFile> validFiles = Arrays.stream(files)
                    .filter(f -> f != null && !f.isEmpty())
                    .limit(maxCount)
                    .toList();
            if (!validFiles.isEmpty()) {
                fileService.uploadFiles(validFiles, "POST", postId, maxCount);
            }
        }

        return "redirect:/site/board/" + boardPath + "/post/" + postId;
    }

    /**
     * 게시글 수정
     */
    @PostMapping("/board/{boardPath}/post/{postId}/update")
    public String updatePost(
            @PathVariable String boardPath,
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(defaultValue = "false") boolean isSecret,
            @RequestParam(required = false) String attachedFileIds,
            @RequestParam(required = false) MultipartFile[] files,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        BoardResponse board = resolveBoard(boardPath);
        if (board == null) {
            redirectAttributes.addFlashAttribute("message", "게시판을 찾을 수 없습니다.");
            return "redirect:/site/";
        }

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);

        PostResponse existing = postService.getPost(postId, userId, roleId);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("message", "게시글을 찾을 수 없습니다.");
            return "redirect:/site/board/" + boardPath;
        }

        String roleCode = resolveRoleCode(session);
        if (!hasPermission(board, roleCode, "canUpdate")) {
            redirectAttributes.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/site/board/" + boardPath + "/post/" + postId;
        }

        boolean isMine = user != null && user.getId() != null && user.getId().equals(existing.getWriterId());
        if (!isMine) {
            redirectAttributes.addFlashAttribute("message", "본인 글만 수정할 수 있습니다.");
            return "redirect:/site/board/" + boardPath + "/post/" + postId;
        }

        List<Long> attachedIds = parseAttachedFileIds(attachedFileIds);
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title(title)
                .content(content != null ? content : "")
                .isSecret(isSecret)
                .attachedFileIds(attachedIds.isEmpty() ? null : attachedIds)
                .build();

        postService.updatePost(postId, request, userId, roleId);

        if (board.getUseFile() != null && board.getUseFile() && files != null && files.length > 0) {
            int maxCount = board.getMaxFileCount() != null ? board.getMaxFileCount() : 5;
            List<MultipartFile> validFiles = Arrays.stream(files)
                    .filter(f -> f != null && !f.isEmpty())
                    .limit(maxCount)
                    .toList();
            if (!validFiles.isEmpty()) {
                fileService.uploadFiles(validFiles, "POST", postId, maxCount);
            }
        }

        return "redirect:/site/board/" + boardPath + "/post/" + postId;
    }

    /**
     * 게시글 삭제
     */
    @PostMapping("/board/{boardPath}/post/{postId}/delete")
    public String deletePost(
            @PathVariable String boardPath,
            @PathVariable Long postId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        BoardResponse board = resolveBoard(boardPath);
        if (board == null) {
            redirectAttributes.addFlashAttribute("message", "게시판을 찾을 수 없습니다.");
            return "redirect:/site/";
        }

        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        Long userId = user != null ? user.getId() : null;
        Long roleId = resolveRoleId(user);

        PostResponse existing = postService.getPost(postId, userId, roleId);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("message", "게시글을 찾을 수 없습니다.");
            return "redirect:/site/board/" + boardPath;
        }

        String roleCode = resolveRoleCode(session);
        if (!hasPermission(board, roleCode, "canDelete")) {
            redirectAttributes.addFlashAttribute("message", "삭제 권한이 없습니다.");
            return "redirect:/site/board/" + boardPath + "/post/" + postId;
        }

        boolean isMine = user != null && user.getId() != null && user.getId().equals(existing.getWriterId());
        if (!isMine) {
            redirectAttributes.addFlashAttribute("message", "본인 글만 삭제할 수 있습니다.");
            return "redirect:/site/board/" + boardPath + "/post/" + postId;
        }

        postService.deletePost(postId, userId, roleId);
        return "redirect:/site/board/" + boardPath;
    }

    private BoardResponse resolveBoard(String boardPath) {
        try {
            Long id = Long.parseLong(boardPath);
            return boardService.getBoard(id);
        } catch (NumberFormatException e) {
            return boardService.getBoardByCode(boardPath);
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
                .map(p -> switch (action) {
                    case "canCreate" -> Boolean.TRUE.equals(p.getCanCreate());
                    case "canRead" -> Boolean.TRUE.equals(p.getCanRead());
                    case "canUpdate" -> Boolean.TRUE.equals(p.getCanUpdate());
                    case "canDelete" -> Boolean.TRUE.equals(p.getCanDelete());
                    default -> false;
                })
                .orElse(false);
    }

    private List<Long> parseAttachedFileIds(String s) {
        if (s == null || s.isBlank()) return Collections.emptyList();
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .map(x -> {
                    try {
                        return Long.parseLong(x);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }
}
