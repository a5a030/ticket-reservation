package com.byunsum.ticket_reservation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignupResponseDto {
    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    public SignupResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
