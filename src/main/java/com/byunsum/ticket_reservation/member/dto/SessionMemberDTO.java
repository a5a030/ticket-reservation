package com.byunsum.ticket_reservation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SessionMemberDTO {
    @Schema(description = "회원 고유 ID (DB 식별자)")
    private Long id;

    @Schema(description = "회원 실명")
    private String username;

    public SessionMemberDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getUserName() {
        return username;
    }

    public Long getId() {
        return id;
    }
}
