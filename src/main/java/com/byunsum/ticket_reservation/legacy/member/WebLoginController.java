package com.byunsum.ticket_reservation.legacy.member;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.jsse.PEMFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web")
public class WebLoginController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebLoginController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String loginId, @RequestParam String password, HttpServletRequest request) {
        try {
            Member member = memberService.findByLoginId(loginId);

            if(!passwordEncoder.matches(password, member.getPassword())) {
                return "redirect:/web/login-form?error";
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("loginMember", member);

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
