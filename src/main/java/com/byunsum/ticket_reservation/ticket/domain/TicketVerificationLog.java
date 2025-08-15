package com.byunsum.ticket_reservation.ticket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class TicketVerificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketCode;
    private String verifier;
    private String deviceInfo;
    private String result;

    private LocalDateTime verifiedAt;

    public TicketVerificationLog() {
    }

    public TicketVerificationLog(String ticketCode, String verifier, String deviceInfo, String result, LocalDateTime verifiedAt) {
        this.ticketCode = ticketCode;
        this.verifier = verifier;
        this.deviceInfo = deviceInfo;
        this.result = result;
        this.verifiedAt = verifiedAt;
    }
}
