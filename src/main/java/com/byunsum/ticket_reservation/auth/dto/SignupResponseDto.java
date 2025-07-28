package com.byunsum.ticket_reservation.auth.dto;

public class SignupResponseDto {
    private String token;

    public SignupResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
