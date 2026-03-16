package com.nt.cms.admin.controller;

import com.nt.cms.board.service.BoardService;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.service.SiteMenuService;
import com.nt.cms.menu.service.SitePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 관리자 메뉴/페이지 관리 웹 컨트롤러
 * 
 * @author CMS Team
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMenuController {

    private final SiteMenuService siteMenuService;
    private final SitePageService sitePageService;
    private final BoardService boardService;

    @GetMapping("/menus")
    public String menuList(Model model) {
        List<SiteMenuResponse> menus = siteMenuService.findListForAdmin();
        model.addAttribute("menus", menus);
        model.addAttribute("currentMenu", "menus");
        model.addAttribute("pageTitle", "메뉴 관리");
        return "admin/menu/list";
    }

    @GetMapping("/menus/new")
    public String menuForm(Model model) {
        List<SiteMenuResponse> parentMenus = siteMenuService.findListForAdmin();
        List<SitePageResponse> pages = sitePageService.findAll();
        
        model.addAttribute("parentMenus", parentMenus);
        model.addAttribute("pages", pages);
        model.addAttribute("boards", boardService.getBoards());
        model.addAttribute("currentMenu", "menus");
        model.addAttribute("pageTitle", "메뉴 등록");
        return "admin/menu/form";
    }

    @GetMapping("/menus/{id}/edit")
    public String menuEditForm(@PathVariable Long id, Model model) {
        SiteMenuResponse menu = siteMenuService.findById(id);
        List<SiteMenuResponse> allMenus = siteMenuService.findListForAdmin();
        Set<Long> excludedIds = Stream.concat(
                Stream.of(menu.getId()),
                siteMenuService.findDescendantIds(id).stream()
        ).collect(Collectors.toSet());
        List<SiteMenuResponse> parentMenus = allMenus.stream()
                .filter(m -> !excludedIds.contains(m.getId()))
                .collect(Collectors.toList());
        List<SitePageResponse> pages = sitePageService.findAll();

        model.addAttribute("menu", menu);
        model.addAttribute("parentMenus", parentMenus);
        model.addAttribute("pages", pages);
        model.addAttribute("boards", boardService.getBoards());
        model.addAttribute("currentMenu", "menus");
        model.addAttribute("pageTitle", "메뉴 수정");
        return "admin/menu/form";
    }

    @GetMapping("/pages")
    public String pageList(Model model) {
        List<SitePageResponse> pages = sitePageService.findAll();
        model.addAttribute("pages", pages);
        model.addAttribute("currentMenu", "pages");
        model.addAttribute("pageTitle", "페이지 관리");
        return "admin/page/list";
    }

    @GetMapping("/pages/new")
    public String pageForm(Model model) {
        model.addAttribute("currentMenu", "pages");
        model.addAttribute("pageTitle", "페이지 등록");
        return "admin/page/form";
    }

    @GetMapping("/pages/{id}/edit")
    public String pageEditForm(@PathVariable Long id, Model model) {
        SitePageResponse page = sitePageService.findById(id);
        model.addAttribute("page", page);
        model.addAttribute("currentMenu", "pages");
        model.addAttribute("pageTitle", "페이지 수정");
        return "admin/page/form";
    }
}
