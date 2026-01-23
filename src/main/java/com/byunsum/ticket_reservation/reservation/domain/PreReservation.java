package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pre_reservation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pre_reservation_member_round",
                columnNames = {"member_id", "performance_round_id"}
        )
)
public class PreReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_round_id", nullable = false)
    private PerformanceRound performanceRound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    public PreReservation() {
    }

    public PreReservation(Member member, PerformanceRound performanceRound, PreReservationStatus status) {
        this.member = member;
        this.performanceRound = performanceRound;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public PerformanceRound getPerformanceRound() {
        return performanceRound;
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

    @PrePersist
    public void prePersist() {
        if(this.appliedAt == null) {
            this.appliedAt = LocalDateTime.now();
        }
    }
}
