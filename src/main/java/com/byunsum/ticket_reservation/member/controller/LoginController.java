package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.form.LoginForm;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginForm loginForm,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            // 기존세션 무효화
            HttpSession oldSession = request.getSession();
            if(oldSession != null) {
                oldSession.invalidate();
            }

            // 로그인 검증
            Member loginMember = memberService.login(loginForm.getLoginId(), loginForm.getPassword());

            // 새 세션 생성
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("loginMember", loginMember);

            Cookie cookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
            cookie.setMaxAge(60 * 60 * 24 * 7); //7일
            cookie.setPath("/");
            response.addCookie(cookie);

//            newSession.setMaxInactiveInterval(30 * 60);

//            return ResponseEntity.ok("로그인 성공");
            return ResponseEntity.ok("/web/members/me"); //프론트에서 리디렉션 처리

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
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
