package com.byunsum.ticket_reservation.auth.dto;

public class RefreshTokenRequestDto {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
