package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.Performance;
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
    private Performance performance;

    @Enumerated(EnumType.STRING)
    private PreReservationStatus status;

    private LocalDateTime appliedAt;

    public PreReservation() {
    }

    public PreReservation(Member member, Performance performance, PreReservationStatus status) {
        this.member = member;
        this.performance = performance;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Performance getPerformance() {
        return performance;
    }

    public PreReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setStatus(PreReservationStatus status) {
        this.status = status;
    }
}
