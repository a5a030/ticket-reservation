package com.byunsum.ticket_reservation.ticket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class TicketReissueLog {
    @Id @GeneratedValue
    private Long id;

    private String oldTicketCode;
    private String newTicketCode;
    private String loginId;
    private String username;
    private LocalDateTime reissueAt;

    public TicketReissueLog() {
    }

    public TicketReissueLog(String oldTicketCode, String newTicketCode, String loginId, String username, LocalDateTime reissueAt) {
        this.oldTicketCode = oldTicketCode;
        this.newTicketCode = newTicketCode;
        this.loginId = loginId;
        this.username = username;
        this.reissueAt = reissueAt;
    }
}
