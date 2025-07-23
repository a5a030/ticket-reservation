package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.form.LoginForm;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final MemberService memberService;

    @Autowired
    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public String login(@RequestBody LoginForm loginForm, HttpServletRequest request) {
        Optional<Member> loginMember = memberService.login(loginForm.getName(), loginForm.getPassword());

        if (loginMember.isEmpty()) {
            return "로그인 실패";
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember.get());

        return "로그인 성공";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "로그아웃 성공";
    }
}
