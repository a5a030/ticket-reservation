package com.byunsum.ticket_reservation.reservation.domain.pre;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pre_reservation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pre_reservation_member_performance",
                columnNames = {"member_id", "performance_id"}
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
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @Column(nullable = true)
    private LocalDateTime drawnAt;

    // 당첨자 결제 권한 만료 시점
    @Column(nullable = true)
    private LocalDateTime expiresAt;

    public PreReservation() {
    }

    public PreReservation(Member member, Performance performance, PreReservationStatus status) {
        this.member = member;
        this.performance = performance;
        this.status = status;
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

    public void markWinner(LocalDateTime drawnAt, LocalDateTime expiresAt) {
        if(drawnAt == null || expiresAt == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if(this.status != PreReservationStatus.WAITING) {
            throw new CustomException(ErrorCode.INVALID_PRE_RESERVATION_STATUS);
        }

        this.status = PreReservationStatus.WINNER;
        this.drawnAt = drawnAt;
        this.expiresAt = expiresAt;
    }

    public void markLoser(LocalDateTime drawnAt) {
        if(drawnAt == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if(this.status != PreReservationStatus.WAITING) {
            throw new CustomException(ErrorCode.INVALID_PRE_RESERVATION_STATUS);
        }

        this.status = PreReservationStatus.LOSER;
        this.drawnAt = drawnAt;
        this.expiresAt = null;
    }

    @PrePersist
    public void prePersist() {
        if(this.appliedAt == null) {
            this.appliedAt = LocalDateTime.now();
        }
    }
}
