package com.nt.cms.admin.controller;

import com.nt.cms.commoncode.dto.CommonCodeGroupResponse;
import com.nt.cms.commoncode.dto.CommonCodeResponse;
import com.nt.cms.commoncode.service.CommonCodeGroupService;
import com.nt.cms.commoncode.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 관리자 공통 코드 웹 컨트롤러
 *
 * @author CMS Team
 */
@Controller
@RequestMapping("/admin/common-codes")
@RequiredArgsConstructor
public class AdminCommonCodeController {

    private final CommonCodeGroupService groupService;
    private final CommonCodeService codeService;

    @GetMapping
    public String list(Model model) {
        List<CommonCodeGroupResponse> groups = groupService.getGroups();
        model.addAttribute("currentMenu", "commoncodes");
        model.addAttribute("pageTitle", "공통 코드");
        model.addAttribute("groups", groups);
        return "admin/commoncode/list";
    }

    @GetMapping("/group/{groupId}")
    public String groupDetail(@PathVariable Long groupId, Model model) {
        CommonCodeGroupResponse group = groupService.getGroup(groupId);
        List<CommonCodeResponse> codes = codeService.getCodesByGroupIdHierarchy(groupId);
        model.addAttribute("currentMenu", "commoncodes");
        model.addAttribute("pageTitle", "공통 코드 - " + group.getGroupName());
        model.addAttribute("group", group);
        model.addAttribute("codes", codes);
        return "admin/commoncode/detail";
    }

    @GetMapping("/group/new")
    public String groupForm(Model model) {
        model.addAttribute("currentMenu", "commoncodes");
        model.addAttribute("pageTitle", "코드 그룹 등록");
        return "admin/commoncode/group-form";
    }

    @GetMapping("/group/{id}/edit")
    public String groupEditForm(@PathVariable Long id, Model model) {
        CommonCodeGroupResponse group = groupService.getGroup(id);
        model.addAttribute("currentMenu", "commoncodes");
        model.addAttribute("pageTitle", "코드 그룹 수정");
        model.addAttribute("group", group);
        return "admin/commoncode/group-form";
    }

    @GetMapping("/group/{groupId}/code/new")
    public String codeForm(@PathVariable Long groupId, Model model) {
        CommonCodeGroupResponse group = groupService.getGroup(groupId);
        List<CommonCodeResponse> codes = codeService.getCodesByGroupId(groupId);
        model.addAttribute("currentMenu", "commoncodes");
        model.addAttribute("pageTitle", "코드 등록 - " + group.getGroupName());
        model.addAttribute("group", group);
        model.addAttribute("parentCodes", codes);
        return "admin/commoncode/code-form";
    }

    @GetMapping("/code/{id}/edit")
    public String codeEditForm(@PathVariable Long id, Model model) {
        CommonCodeResponse code = codeService.getCode(id);
        CommonCodeGroupResponse group = groupService.getGroup(code.getGroupId());
        List<CommonCodeResponse> codes = codeService.getCodesByGroupId(code.getGroupId());
        // 수정 시 상위 코드 선택에서 자기 자신·자손 제외 (순환 방지)
        Set<Long> excludeIds = new HashSet<>(codeService.findDescendantIds(id));
        excludeIds.add(id);
        List<CommonCodeResponse> parentCodes = codes.stream()
                .filter(c -> !excludeIds.contains(c.getId()))
                .toList();
        model.addAttribute("currentMenu", "commoncodes");
        model.addAttribute("pageTitle", "코드 수정 - " + code.getCodeName());
        model.addAttribute("code", code);
        model.addAttribute("group", group);
        model.addAttribute("parentCodes", parentCodes);
        return "admin/commoncode/code-form";
    }
}
