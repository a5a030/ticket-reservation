package com.byunsum.ticket_reservation.auth.service;

import com.byunsum.ticket_reservation.auth.dto.*;
import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import com.byunsum.ticket_reservation.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(MemberService memberService, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = tokenProvider;
    }

    @Transactional
    public LoginResponseDto signup(SignupRequestDto dto) {
        Member member = new Member();
        member.setLoginId(dto.getLoginId());
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setRole("ROLE_USER");

        memberService.join(member);

        String accessToken = jwtTokenProvider.createToken(member.getLoginId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getLoginId(), member.getRole());
        memberService.updateRefreshToken(member.getLoginId(), refreshToken);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        Member member = memberService.findByLoginId(dto.getLoginId());

        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createToken(member.getLoginId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getLoginId(), member.getRole());
        memberService.updateRefreshToken(member.getLoginId(), refreshToken);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public RefreshTokenResponseDto refresh(RefreshTokenRequestDto dto) {
        String refreshToken = dto.getRefreshToken();

        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, INVALID_REFRESH_TOKEN);
        }

        String loginId = jwtTokenProvider.getName(refreshToken);
        Member member = memberService.findByLoginId(loginId);

        if(member.getRefreshToken() == null || !refreshToken.equals(member.getRefreshToken())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createToken(member.getLoginId(), member.getRole());

        return  new RefreshTokenResponseDto(newAccessToken, refreshToken);
    }
}
