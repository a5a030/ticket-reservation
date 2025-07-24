package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebMyPageController {
    @GetMapping("/web/members/me")
    public String myPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginMember") == null){
            // 로그인돼 있지 않으면 로그인 페이지로 리디렉션
            return "redirect:/web/login-form?error=unauthorized";
        }

        Member loginMember = (Member) session.getAttribute("loginMember");
        model.addAttribute("member", loginMember);

        return "myPage";
    }
}
