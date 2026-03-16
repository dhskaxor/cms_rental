package com.nt.cms.admin.controller;

import com.nt.cms.role.dto.PermissionResponse;
import com.nt.cms.role.dto.RoleResponse;
import com.nt.cms.role.service.PermissionService;
import com.nt.cms.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자 역할/권한 관리 웹 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;

    @GetMapping
    public String list(Model model) {
        List<RoleResponse> roles = roleService.getRoles();
        model.addAttribute("currentMenu", "roles");
        model.addAttribute("pageTitle", "역할 관리");
        model.addAttribute("roles", roles);
        return "admin/role/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        RoleResponse role = roleService.getRole(id);
        model.addAttribute("currentMenu", "roles");
        model.addAttribute("pageTitle", "역할 상세");
        model.addAttribute("role", role);
        return "admin/role/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        List<PermissionResponse> permissions = permissionService.getPermissions();
        model.addAttribute("currentMenu", "roles");
        model.addAttribute("pageTitle", "역할 등록");
        model.addAttribute("permissions", permissions);
        return "admin/role/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        RoleResponse role = roleService.getRole(id);
        List<PermissionResponse> permissions = permissionService.getPermissions();
        model.addAttribute("currentMenu", "roles");
        model.addAttribute("pageTitle", "역할 수정");
        model.addAttribute("role", role);
        model.addAttribute("permissions", permissions);
        return "admin/role/form";
    }
}
