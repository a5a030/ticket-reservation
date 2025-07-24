package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web")
public class WebLoginController {
    private final MemberService memberService;

    @Autowired
    public WebLoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String name, @RequestParam String password, HttpServletRequest request) {
        try {
            Member loginMember = memberService.login(name, password);
            HttpSession session = request.getSession(true);
            session.setAttribute("loginMember", loginMember);

            return  "redirect:/web/members/me";
        } catch (IllegalArgumentException e) {
            return "redirect:/web/login-form?error";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            session.invalidate();
        }

        return "redirect:/web/login-form";
    }
}
