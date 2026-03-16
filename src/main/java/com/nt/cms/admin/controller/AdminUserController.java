package com.nt.cms.admin.controller;

import com.nt.cms.common.response.PageResponse;
import com.nt.cms.role.dto.RoleResponse;
import com.nt.cms.role.service.RoleService;
import com.nt.cms.user.dto.UserResponse;
import com.nt.cms.user.dto.UserSearchRequest;
import com.nt.cms.user.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 사용자 관리 웹 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;

    /**
     * 사용자 목록 페이지
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        UserSearchRequest searchRequest = UserSearchRequest.builder()
                .keyword(keyword)
                .roleId(roleId)
                .status(status)
                .page(page)
                .size(size)
                .build();

        PageResponse<UserResponse> users = userService.getUsers(searchRequest);

        model.addAttribute("currentMenu", "users");
        model.addAttribute("pageTitle", "사용자 관리");
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("status", status);

        return "admin/user/list";
    }

    /**
     * 사용자 상세 페이지
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        UserResponse user = userService.getUser(id);

        model.addAttribute("currentMenu", "users");
        model.addAttribute("pageTitle", "사용자 상세");
        model.addAttribute("user", user);

        return "admin/user/detail";
    }

    /**
     * 사용자 등록 폼
     */
    @GetMapping("/new")
    public String createForm(Model model) {
        List<RoleResponse> roles = roleService.getRoles();
        model.addAttribute("currentMenu", "users");
        model.addAttribute("pageTitle", "사용자 등록");
        model.addAttribute("roles", roles);

        return "admin/user/form";
    }

    /**
     * 사용자 수정 폼
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        UserResponse user = userService.getUser(id);
        List<RoleResponse> roles = roleService.getRoles();

        model.addAttribute("currentMenu", "users");
        model.addAttribute("pageTitle", "사용자 수정");
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "admin/user/form";
    }
}
