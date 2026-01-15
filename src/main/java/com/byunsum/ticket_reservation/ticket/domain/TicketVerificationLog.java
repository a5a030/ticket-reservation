package com.byunsum.ticket_reservation.ticket.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TicketVerificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketCode;
    private String verifier;
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketVerifyResult result;

    private LocalDateTime verifiedAt;

    public TicketVerificationLog() {
    }

    public TicketVerificationLog(String ticketCode, String verifier, String ipAddress, TicketVerifyResult result, LocalDateTime verifiedAt) {
        this.ticketCode = ticketCode;
        this.verifier = verifier;
        this.ipAddress = ipAddress;
        this.result = result;
        this.verifiedAt = verifiedAt;
    }
}
