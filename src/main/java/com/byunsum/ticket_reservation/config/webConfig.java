package com.byunsum.ticket_reservation.config;


import com.byunsum.ticket_reservation.member.interceptor.LoginCheckInterceptor;
import com.byunsum.ticket_reservation.member.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webConfig implements WebMvcConfigurer {
    private final MemberService memberService;

    public webConfig(MemberService memberService) {
        this.memberService = memberService;
    }

    @Bean
    public LoginCheckInterceptor loginCheckInterceptor(MemberService memberService) {
        return new LoginCheckInterceptor(memberService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor(memberService))
                .order(1)
                .addPathPatterns("/members/me", "/reservation/**", "/mypage/**")
                .excludePathPatterns("/", "/login", "/logout", "/members/new", "/css/**", "/js/**", "/favicon.ico");
    }
}
