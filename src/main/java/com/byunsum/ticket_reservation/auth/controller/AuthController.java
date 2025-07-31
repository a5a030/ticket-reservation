package com.byunsum.ticket_reservation.auth.controller;

import com.byunsum.ticket_reservation.auth.dto.*;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.service.MemberService;
import com.byunsum.ticket_reservation.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증 API", description = "JWT 로그인 / 회원가입 / 리프레시 토큰 관련 API")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "회원가입", description = "사용자 정보를 입력받아 회원가입을 수행하고 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 및 토큰 발급 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값")
    })
    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        memberService.register(requestDto.getName(), requestDto.getPassword(), requestDto.getEmail());

        Member member = memberService.findByName(requestDto.getName());

        String accessToken = jwtTokenProvider.createToken(member.getName(),  member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getName(),  member.getRole());

        memberService.updateRefreshToken(member.getName(),  refreshToken);

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @Operation(summary = "로그인", description = "사용자의 이름과 비밀번호로 로그인을 수행하고 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 잘못됨")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        Member member = memberService.login(requestDto.getName(), requestDto.getPassword());
        String accessToken = jwtTokenProvider.createToken(member.getUsername(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getUsername(), member.getRole());

        memberService.updateRefreshToken(member.getName(), refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @Operation(summary = "리프레시 토큰 재발급", description = "Refresh Token을 검증하고 새로운 Access Token을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 유효성 실패")
    })
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
