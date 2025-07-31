package com.byunsum.ticket_reservation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignupRequestDto {
    @Schema(description = "사용자 이름")
    private String name;

    @Schema(name = "비밀번호")
    private String password;

    @Schema(name = "이메일")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
