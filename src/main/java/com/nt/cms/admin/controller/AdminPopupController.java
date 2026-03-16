package com.nt.cms.admin.controller;

import com.nt.cms.common.response.PageResponse;
import com.nt.cms.popup.dto.PopupResponse;
import com.nt.cms.popup.dto.PopupSearchRequest;
import com.nt.cms.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 팝업 웹 컨트롤러
 *
 * @author CMS Team
 */
@Controller
@RequestMapping("/admin/popups")
@RequiredArgsConstructor
public class AdminPopupController {

    private final PopupService popupService;

    @GetMapping
    public String list(@ModelAttribute PopupSearchRequest request, Model model) {
        PageResponse<PopupResponse> page = popupService.getPopups(request);
        model.addAttribute("currentMenu", "popups");
        model.addAttribute("pageTitle", "팝업 관리");
        model.addAttribute("popups", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("search", request);
        return "admin/popup/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("currentMenu", "popups");
        model.addAttribute("pageTitle", "팝업 등록");
        return "admin/popup/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PopupResponse popup = popupService.getPopup(id);
        model.addAttribute("currentMenu", "popups");
        model.addAttribute("pageTitle", "팝업 상세 - " + popup.getPopupName());
        model.addAttribute("popup", popup);
        return "admin/popup/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PopupResponse popup = popupService.getPopup(id);
        model.addAttribute("currentMenu", "popups");
        model.addAttribute("pageTitle", "팝업 수정");
        model.addAttribute("popup", popup);
        return "admin/popup/form";
    }
}
