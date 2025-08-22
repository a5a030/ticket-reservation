package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PreReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private PerformanceRound round;

    @Enumerated(EnumType.STRING)
    private PreReservationStatus status;

    private LocalDateTime appliedAt;

    public PreReservation() {
    }

    public PreReservation(Member member, PerformanceRound round, PreReservationStatus status) {
        this.member = member;
        this.round = round;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public PerformanceRound getRound() {
        return round;
    }

    public PreReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
}
