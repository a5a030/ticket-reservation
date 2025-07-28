package com.byunsum.ticket_reservation.auth.dto;

public class RefreshTokenResponseDto {
    private String accessToken;
    private String refreshToken;

    public RefreshTokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
