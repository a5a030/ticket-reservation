package com.byunsum.ticket_reservation.config;

import com.byunsum.ticket_reservation.config.interceptor.LoginCheckInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class webConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/members/me", "/reservation/**", "/mypage/**")
                .excludePathPatterns("/", "/login", "/logout", "/members/new", "/css/**", "/js/**", "/favicon.ico");
    }
}
