package com.byunsum.ticket_reservation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequestDto {
    @Schema(name = "아이디")
    private String loginId;

    @Schema(name = "비밀번호")
    private String password;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
