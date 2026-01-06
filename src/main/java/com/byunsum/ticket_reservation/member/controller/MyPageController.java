package com.byunsum.ticket_reservation.member.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.dto.MyPageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "마이페이지 API", description = "로그인한 회원 정보 조회 API")
@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공")
    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal Member member) {
        MyPageResponseDto responseDto = new MyPageResponseDto(
                member.getLoginId(),
                member.getUsername(),
                member.getEmail(),
                member.getRole()
        );

        return ResponseEntity.ok(responseDto);
    }
}
