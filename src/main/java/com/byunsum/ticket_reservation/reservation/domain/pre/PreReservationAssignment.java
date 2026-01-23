package com.byunsum.ticket_reservation.reservation.domain.pre;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pre_reservation_assignment",
        uniqueConstraints = {
                // 한 응모(=당첨권) 당 배정은 1개 (좌석추첨제: 1인 1좌석 가정)
                @UniqueConstraint(
                        name = "uk_assignment_pre_reservation",
                        columnNames = {"pre_reservation_id"}
                ),
                // 한 좌석은 동시에 1명에게만 배정
                @UniqueConstraint(
                        name = "uk_assignment_seat",
                        columnNames = {"seat_id"}
                )
        },
        indexes = {
                @Index(name = "idx_assignment_expires_at", columnList = "expires_at"),
                @Index(name = "idx_assignment_status", columnList = "status")
        }
)
public class PreReservationAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_reservation_id", nullable = false)
    private PreReservation preReservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreReservationAssignmentStatus status;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    public PreReservationAssignment() {
    }

    public PreReservationAssignment(PreReservation preReservation, Seat seat, LocalDateTime expiresAt) {
        if(preReservation == null || seat == null || expiresAt == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.preReservation = preReservation;
        this.seat = seat;
        this.status = PreReservationAssignmentStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public PreReservation getPreReservation() {
        return preReservation;
    }

    public Seat getSeat() {
        return seat;
    }

    public Payment getPayment() {
        return payment;
    }

    public PreReservationAssignmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiresAt != null && now.isAfter(this.expiresAt);
    }

    public void markPaid(Payment payment) {
        if(payment == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if(this.status != PreReservationAssignmentStatus.ASSIGNED) {
            throw new CustomException(ErrorCode.INVALID_PRE_RESERVATION_ASSIGNMENT_STATUS);
        }

        if(this.payment != null) {
            throw new CustomException(ErrorCode.PRE_RESERVATION_ASSIGNMENT_ALREADY_PROCESSED);
        }

        this.payment = payment;
        this.status = PreReservationAssignmentStatus.PAID;
    }

    public void markCancelled() {
        if(this.status != PreReservationAssignmentStatus.ASSIGNED) {
            throw new CustomException(ErrorCode.PRE_RESERVATION_ASSIGNMENT_ALREADY_PROCESSED);
        }

        this.status = PreReservationAssignmentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markExpired() {
        if(this.status != PreReservationAssignmentStatus.ASSIGNED) {
            throw new CustomException(ErrorCode.PRE_RESERVATION_ASSIGNMENT_ALREADY_PROCESSED);
        }

        this.status = PreReservationAssignmentStatus.EXPIRED;
        this.expiredAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = PreReservationAssignmentStatus.ASSIGNED;
        }

        if(this.expiresAt == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
