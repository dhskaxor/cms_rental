package com.nt.cms.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/rental")
public class AdminRentalController {

    @GetMapping("/places/view")
    public String places(Model model) {
        model.addAttribute("currentMenu", "rental-places");
        model.addAttribute("pageTitle", "예약 장소 관리");
        return "admin/rental/places";
    }

    @GetMapping("/rooms/view")
    public String rooms(Model model) {
        model.addAttribute("currentMenu", "rental-rooms");
        model.addAttribute("pageTitle", "예약 공간 관리");
        return "admin/rental/rooms";
    }

    @GetMapping("/pricing/view")
    public String pricing(Model model) {
        model.addAttribute("currentMenu", "rental-pricing");
        model.addAttribute("pageTitle", "예약 요금 관리");
        return "admin/rental/pricing";
    }

    @GetMapping("/closed-rules/view")
    public String closedRules(Model model) {
        model.addAttribute("currentMenu", "rental-closed-rules");
        model.addAttribute("pageTitle", "예약 휴관/공휴일 관리");
        return "admin/rental/closed-rules";
    }

    @GetMapping("/unavailable-slots/view")
    public String unavailableSlotsRedirect() {
        return "redirect:/admin/rental/pricing/view";
    }

    @GetMapping("/calendar/view")
    public String calendar(Model model) {
        model.addAttribute("currentMenu", "rental-calendar");
        model.addAttribute("pageTitle", "예약 관리 - 달력");
        return "admin/rental/calendar";
    }

    @GetMapping("/reservations/view")
    public String reservations(Model model) {
        model.addAttribute("currentMenu", "rental-reservations");
        model.addAttribute("pageTitle", "예약 관리 - 목록");
        return "admin/rental/reservations";
    }
}

