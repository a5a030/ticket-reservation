package com.byunsum.ticket_reservation.member.dto;

public class SessionMemberDTO {
    private Long id;
    private String name;

    public SessionMemberDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
