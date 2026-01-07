package com.byunsum.ticket_reservation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignupResponseDto {
    @Schema(description = "회원 ID")
    private Long id;

    @Schema(description = "로그인 ID")
    private String loginId;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "이메일")
    private String email;

    public SignupResponseDto(Long id, String loginId, String name, String email) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getLoginId() { return loginId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
