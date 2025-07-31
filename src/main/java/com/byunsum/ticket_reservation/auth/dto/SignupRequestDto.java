package com.byunsum.ticket_reservation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignupRequestDto {
    @Schema(description = "아이디")
    private String loginId;

    @Schema(description = "이름")
    private String username;

    @Schema(name = "비밀번호")
    private String password;

    @Schema(name = "이메일")
    private String email;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
