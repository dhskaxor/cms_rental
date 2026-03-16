package com.nt.cms.admin.controller;

import com.nt.cms.board.dto.BoardGroupResponse;
import com.nt.cms.board.dto.BoardResponse;
import com.nt.cms.board.service.BoardGroupService;
import com.nt.cms.board.service.BoardService;
import com.nt.cms.role.dto.RoleResponse;
import com.nt.cms.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 관리자 게시판 관리 웹 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/admin/boards")
@RequiredArgsConstructor
public class AdminBoardController {

    private final BoardService boardService;
    private final BoardGroupService boardGroupService;
    private final RoleService roleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentMenu", "boards");
        model.addAttribute("pageTitle", "게시판 관리");
        List<BoardResponse> boards = boardService.getBoards();
        model.addAttribute("boards", boards);
        return "admin/board/list";
    }

    /** 리터럴 경로를 /{id}보다 먼저 선언하여 경로 매칭 충돌 방지 */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("currentMenu", "boards");
        model.addAttribute("pageTitle", "게시판 생성");
        List<BoardGroupResponse> groups = boardGroupService.getGroups();
        List<RoleResponse> roles = roleService.getRoles();
        model.addAttribute("groups", groups);
        model.addAttribute("roles", roles);
        model.addAttribute("board", null);
        return "admin/board/form";
    }

    @GetMapping("/groups")
    public String groupList(Model model) {
        model.addAttribute("currentMenu", "boards");
        model.addAttribute("pageTitle", "게시판 그룹 관리");
        List<BoardGroupResponse> groups = boardGroupService.getGroups();
        model.addAttribute("groups", groups);
        return "admin/board/group-list";
    }

    @GetMapping("/groups/new")
    public String groupForm(Model model) {
        model.addAttribute("currentMenu", "boards");
        model.addAttribute("pageTitle", "그룹 생성");
        model.addAttribute("group", null);
        return "admin/board/group-form";
    }

    @GetMapping("/groups/{id}/edit")
    public String groupEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("currentMenu", "boards");
        model.addAttribute("pageTitle", "그룹 수정");
        BoardGroupResponse group = boardGroupService.getGroup(id);
        model.addAttribute("group", group);
        return "admin/board/group-form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("currentMenu", "boards");
        model.addAttribute("pageTitle", "게시판 상세");
        BoardResponse board = boardService.getBoard(id);
        model.addAttribute("board", board);
        return "admin/board/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("currentMenu", "boards");
        model.addAttribute("pageTitle", "게시판 수정");
        BoardResponse board = boardService.getBoard(id);
        List<BoardGroupResponse> groups = boardGroupService.getGroups();
        List<RoleResponse> roles = roleService.getRoles();
        model.addAttribute("board", board);
        model.addAttribute("groups", groups);
        model.addAttribute("roles", roles);
        return "admin/board/form";
    }
}
