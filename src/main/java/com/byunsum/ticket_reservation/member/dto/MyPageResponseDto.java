package com.byunsum.ticket_reservation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class MyPageResponseDto {
    @Schema(description = "아이디")
    private String loginId;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "권한", example = "ROLE_USER")
    private String role;

    public MyPageResponseDto(String loginId, String name, String email, String role) {
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
