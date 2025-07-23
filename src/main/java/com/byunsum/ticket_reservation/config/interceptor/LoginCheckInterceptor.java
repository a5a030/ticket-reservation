package com.byunsum.ticket_reservation.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginMember") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
            response.getWriter().write("로그인이 필요한 요청입니다.");
            return false;
        }

        return true;

    }
}
