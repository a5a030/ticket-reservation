package com.byunsum.ticket_reservation.member.interceptor;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

public class LoginCheckInterceptor implements HandlerInterceptor {
    private final MemberService memberService;


    public LoginCheckInterceptor(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("loginMember") != null) {
            return true;
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            Cookie memberIdCookie = Arrays.stream(cookies)
                    .filter(cookie -> "memberId".equals(cookie.getName()))
                    .findFirst().orElse(null);

            if(memberIdCookie != null) {
                try {
                    Long memberId = Long.parseLong(memberIdCookie.getValue());
                    Member member = memberService.findOne(memberId).orElse(null);

                    if (member != null) {
                        session = request.getSession(true);
                        session.setAttribute("loginMember", member);

                        return true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        response.sendRedirect("/web/login-form?error=session-expired");
        return false;
    }
}
