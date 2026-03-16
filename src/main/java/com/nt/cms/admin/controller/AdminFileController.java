package com.nt.cms.admin.controller;

import com.nt.cms.common.response.PageResponse;
import com.nt.cms.file.dto.FileResponse;
import com.nt.cms.file.dto.FileSearchRequest;
import com.nt.cms.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 파일 웹 컨트롤러
 *
 * @author CMS Team
 */
@Controller
@RequestMapping("/admin/files")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileService fileService;

    @GetMapping
    public String list(@ModelAttribute FileSearchRequest request, Model model) {
        PageResponse<FileResponse> page = fileService.getFilesForAdmin(request);
        model.addAttribute("currentMenu", "files");
        model.addAttribute("pageTitle", "파일 관리");
        model.addAttribute("files", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("search", request);
        return "admin/file/list";
    }
}
