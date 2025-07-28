package com.byunsum.ticket_reservation.auth.controller;

import com.byunsum.ticket_reservation.auth.dto.LoginRequestDto;
import com.byunsum.ticket_reservation.auth.dto.LoginResponseDto;
import com.byunsum.ticket_reservation.auth.dto.SignupRequestDto;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import com.byunsum.ticket_reservation.security.jwt.JwtTokenProvider;
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
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto requestDto) {
        memberService.register(requestDto.getName(), requestDto.getPassword(), requestDto.getEmail());

        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        Member member = memberService.login(requestDto.getName(), requestDto.getPassword());
        String token = jwtTokenProvider.createToken(member.getUsername(), member.getRole());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
