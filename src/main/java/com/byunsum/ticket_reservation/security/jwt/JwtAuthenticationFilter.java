package com.byunsum.ticket_reservation.security.jwt;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, MemberService memberService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("[JwtFilter] 요청 URI: " + request.getRequestURI());
        String token = resolveToken(request);
        System.out.println("[JwtFilter] 토큰: " + token);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            filterChain.doFilter(request, response); //바로 통과시켜야 permitAll 경로가 막히지 않음
            return;
        }

        String  name = jwtTokenProvider.getName(token);
        String  role = jwtTokenProvider.getRole(token);

        Member member = (Member) memberService.loadUserByUsername(name); //db에서 사용자 조회
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member, null, jwtTokenProvider.getAuthorities(token));

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }

        return null;
    }
}
