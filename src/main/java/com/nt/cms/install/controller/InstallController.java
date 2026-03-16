package com.nt.cms.install.controller;

import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.install.dto.AdminAccountRequest;
import com.nt.cms.install.dto.DatabaseConfigRequest;
import com.nt.cms.install.dto.SiteConfigRequest;
import com.nt.cms.install.service.InstallService;
import com.nt.cms.common.util.PasswordUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * 설치 마법사 컨트롤러
 * 웹 기반 설치 UI 제공
 * 
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/install")
@RequiredArgsConstructor
public class InstallController {

    private final InstallService installService;

    /**
     * 설치 시작 페이지 (Step 1: 데이터베이스 설정)
     */
    @GetMapping
    public String index(Model model) {
        // 이미 설치 완료된 경우 차단
        if (installService.isInstalled()) {
            return "install/already-installed";
        }
        
        model.addAttribute("databaseConfig", new DatabaseConfigRequest());
        model.addAttribute("currentStep", 1);
        return "install/step1-database";
    }

    /**
     * 데이터베이스 연결 테스트 (AJAX)
     */
    @PostMapping("/api/test-connection")
    @ResponseBody
    public ApiResponse<Map<String, Object>> testConnection(@Valid @RequestBody DatabaseConfigRequest request) {
        boolean success = installService.testDatabaseConnection(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("connected", success);
        result.put("message", success ? "데이터베이스 연결 성공!" : "연결 실패. 정보를 확인해주세요.");
        
        return ApiResponse.success(result);
    }

    /**
     * Step 1 처리: 데이터베이스 설정 저장
     */
    @PostMapping("/step1")
    public String processStep1(
            @Valid @ModelAttribute("databaseConfig") DatabaseConfigRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentStep", 1);
            return "install/step1-database";
        }

        // 연결 테스트
        if (!installService.testDatabaseConnection(request)) {
            model.addAttribute("error", "데이터베이스 연결에 실패했습니다. 정보를 확인해주세요.");
            model.addAttribute("currentStep", 1);
            return "install/step1-database";
        }

        // 설정 저장
        installService.saveDatabaseConfig(request);
        redirectAttributes.addFlashAttribute("success", "데이터베이스 연결 성공!");
        
        return "redirect:/install/step2";
    }

    /**
     * Step 2: 스키마 실행 페이지
     */
    @GetMapping("/step2")
    public String step2(Model model) {
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }
        
        if (installService.getDatabaseConfig() == null) {
            return "redirect:/install";
        }

        model.addAttribute("currentStep", 2);
        model.addAttribute("databaseConfig", installService.getDatabaseConfig());
        return "install/step2-schema";
    }

    /**
     * Step 2 처리: 스키마 실행
     */
    @PostMapping("/step2")
    public String processStep2(RedirectAttributes redirectAttributes) {
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }

        // 스키마 실행
        try {
            if (!installService.executeSchema()) {
                redirectAttributes.addFlashAttribute("error", "테이블 생성에 실패했습니다.");
                return "redirect:/install/step2";
            }
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", "테이블 생성 실패: " + e.getMessage());
            return "redirect:/install/step2";
        }

        // 초기 데이터 삽입
        try {
            if (!installService.insertInitialData()) {
                redirectAttributes.addFlashAttribute("error", "초기 데이터 생성에 실패했습니다.");
                return "redirect:/install/step2";
            }
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", "초기 데이터 삽입 실패: " + e.getMessage());
            return "redirect:/install/step2";
        }

        redirectAttributes.addFlashAttribute("success", "테이블 및 초기 데이터 생성 완료!");
        return "redirect:/install/step3";
    }

    /**
     * Step 3: 관리자 계정 생성 페이지
     */
    @GetMapping("/step3")
    public String step3(Model model) {
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }
        
        if (installService.getDatabaseConfig() == null) {
            return "redirect:/install";
        }

        model.addAttribute("adminAccount", new AdminAccountRequest());
        model.addAttribute("currentStep", 3);
        return "install/step3-admin";
    }

    /**
     * Step 3 처리: 관리자 계정 생성
     */
    @PostMapping("/step3")
    public String processStep3(
            @Valid @ModelAttribute("adminAccount") AdminAccountRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentStep", 3);
            return "install/step3-admin";
        }

        // 비밀번호 확인
        if (!request.isPasswordMatch()) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("currentStep", 3);
            return "install/step3-admin";
        }

        // 비밀번호 정책 검증
        if (!PasswordUtil.isValid(request.getPassword())) {
            model.addAttribute("error", PasswordUtil.getViolationReason(request.getPassword()));
            model.addAttribute("currentStep", 3);
            return "install/step3-admin";
        }

        // 관리자 계정 생성
        if (!installService.createAdminAccount(request)) {
            model.addAttribute("error", "관리자 계정 생성에 실패했습니다.");
            model.addAttribute("currentStep", 3);
            return "install/step3-admin";
        }

        redirectAttributes.addFlashAttribute("success", "관리자 계정 생성 완료!");
        return "redirect:/install/step4";
    }

    /**
     * Step 4: 사이트 설정 페이지
     */
    @GetMapping("/step4")
    public String step4(Model model) {
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }
        
        if (installService.getDatabaseConfig() == null) {
            return "redirect:/install";
        }

        model.addAttribute("siteConfig", new SiteConfigRequest());
        model.addAttribute("currentStep", 4);
        return "install/step4-config";
    }

    /**
     * Step 4 처리: 사이트 설정 저장
     */
    @PostMapping("/step4")
    public String processStep4(
            @Valid @ModelAttribute("siteConfig") SiteConfigRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentStep", 4);
            return "install/step4-config";
        }

        installService.saveSiteConfig(request);
        redirectAttributes.addFlashAttribute("success", "사이트 설정 저장 완료!");
        return "redirect:/install/step5";
    }

    /**
     * Step 5: 설치 완료 페이지
     */
    @GetMapping("/step5")
    public String step5(Model model) {
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }
        
        if (installService.getDatabaseConfig() == null) {
            return "redirect:/install";
        }

        model.addAttribute("currentStep", 5);
        return "install/step5-complete";
    }

    /**
     * Step 5 처리: 설치 완료
     */
    @PostMapping("/step5")
    public String processStep5(RedirectAttributes redirectAttributes) {
        if (installService.isInstalled()) {
            return "redirect:/install/already-installed";
        }

        if (!installService.completeInstallation()) {
            redirectAttributes.addFlashAttribute("error", "설치 완료 처리에 실패했습니다.");
            return "redirect:/install/step5";
        }

        return "redirect:/install/success";
    }

    /**
     * 설치 성공 페이지
     */
    @GetMapping("/success")
    public String success() {
        return "install/success";
    }

    /**
     * 이미 설치됨 페이지
     */
    @GetMapping("/already-installed")
    public String alreadyInstalled() {
        return "install/already-installed";
    }
}
