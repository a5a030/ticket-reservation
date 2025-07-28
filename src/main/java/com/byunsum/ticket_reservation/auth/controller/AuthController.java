package com.byunsum.ticket_reservation.auth.controller;

import com.byunsum.ticket_reservation.auth.dto.*;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import com.byunsum.ticket_reservation.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        memberService.register(requestDto.getName(), requestDto.getPassword(), requestDto.getEmail());

        Member member = memberService.findByName(requestDto.getName());

        String accessToken = jwtTokenProvider.createToken(member.getName(),  member.getRole());
        String refreshToken = jwtTokenProvider.createToken(member.getName(),  member.getRole());

        memberService.updateRefreshToken(member.getName(),  refreshToken);

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        Member member = memberService.login(requestDto.getName(), requestDto.getPassword());
        String acceessToken = jwtTokenProvider.createToken(member.getUsername(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getUsername(), member.getRole());


        return ResponseEntity.ok(new LoginResponseDto(acceessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto dto) {
        String refreshToken = dto.getRefreshToken();

        if(!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String name = jwtTokenProvider.getName(refreshToken);
        Member member = memberService.findByName(name);

        if(member.getRefreshToken() == null || !refreshToken.equals(member.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String newAccessToken = jwtTokenProvider.createToken(member.getUsername(), member.getRole());

        return ResponseEntity.ok(new RefreshTokenResponseDto(newAccessToken, refreshToken));
    }
}
