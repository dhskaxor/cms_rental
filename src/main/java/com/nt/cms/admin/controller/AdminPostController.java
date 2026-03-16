package com.nt.cms.admin.controller;

import com.nt.cms.board.dto.BoardResponse;
import com.nt.cms.board.dto.PostResponse;
import com.nt.cms.board.dto.PostSearchRequest;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.board.service.PostService;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.service.FileService;
import com.nt.cms.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.nt.cms.auth.security.CustomUserDetails;

import java.util.List;

/**
 * 관리자 게시글 관리 웹 컨트롤러
 *
 * @author CMS Team
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPostController {

    private final BoardService boardService;
    private final PostService postService;
    private final FileService fileService;

    /**
     * 게시판 선택 페이지 (게시판 목록에서 선택 후 게시글 목록으로 이동)
     */
    @GetMapping("/posts")
    public String boardSelect(Model model) {
        model.addAttribute("currentMenu", "posts");
        model.addAttribute("pageTitle", "게시글 관리");
        List<BoardResponse> boards = boardService.getBoards();
        model.addAttribute("boards", boards);
        return "admin/post/board-select";
    }

    /**
     * 게시판별 게시글 목록
     */
    @GetMapping("/boards/{boardId}/posts")
    public String list(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        model.addAttribute("currentMenu", "posts");
        model.addAttribute("pageTitle", "게시글 목록");

        BoardResponse board = boardService.getBoard(boardId);
        model.addAttribute("board", board);

        PostSearchRequest request = PostSearchRequest.builder()
                .page(page)
                .size(size)
                .searchType(searchType)
                .keyword(keyword)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        PageResponse<PostResponse> pageResponse = postService.getPosts(boardId, request);
        model.addAttribute("pageResponse", pageResponse);
        model.addAttribute("posts", pageResponse.getContent());
        model.addAttribute("searchRequest", request);

        return "admin/post/list";
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/boards/{boardId}/posts/{postId}")
    public String detail(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        model.addAttribute("currentMenu", "posts");
        model.addAttribute("pageTitle", "게시글 상세");

        BoardResponse board = boardService.getBoard(boardId);
        model.addAttribute("board", board);

        Long userId = userDetails != null ? userDetails.getUserId() : null;
        Long roleId = userDetails != null ? userDetails.getRoleId() : null;
        PostResponse post = postService.getPost(postId, userId, roleId);
        model.addAttribute("post", post);

        return "admin/post/detail";
    }

    /**
     * 게시글 등록 폼
     */
    @GetMapping("/boards/{boardId}/posts/new")
    public String createForm(@PathVariable Long boardId, Model model) {
        model.addAttribute("currentMenu", "posts");
        model.addAttribute("pageTitle", "게시글 등록");

        BoardResponse board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        model.addAttribute("post", null);

        return "admin/post/form";
    }

    /**
     * 게시글 수정 폼
     */
    @GetMapping("/boards/{boardId}/posts/{postId}/edit")
    public String editForm(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        model.addAttribute("currentMenu", "posts");
        model.addAttribute("pageTitle", "게시글 수정");

        BoardResponse board = boardService.getBoard(boardId);
        model.addAttribute("board", board);

        Long userId = userDetails != null ? userDetails.getUserId() : null;
        Long roleId = userDetails != null ? userDetails.getRoleId() : null;
        PostResponse post = postService.getPost(postId, userId, roleId);
        model.addAttribute("post", post);

        List<FileResponse> existingFiles = fileService.getFilesByRef("POST", postId);
        model.addAttribute("existingFiles", existingFiles);

        return "admin/post/form";
    }
}
