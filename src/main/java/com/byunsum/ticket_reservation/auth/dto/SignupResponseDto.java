package com.byunsum.ticket_reservation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignupResponseDto {
    @Schema(description = "회원 ID")
    private Long id;

    @Schema(description = "로그인 ID")
    private String loginId;

    @Schema(description = "이름")
    private String username;

    @Schema(description = "이메일")
    private String email;

    public SignupResponseDto(Long id, String loginId, String username, String email) {
        this.id = id;
        this.loginId = loginId;
        this.username = username;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getLoginId() { return loginId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
