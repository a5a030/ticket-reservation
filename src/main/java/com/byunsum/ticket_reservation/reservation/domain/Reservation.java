package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationCode;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public boolean isCancelled() {
        return this.status == ReservationStatus.CANCELLED;
    }

    private boolean reconfirmed = false; // 재확정 1회 한정 허용

    //예매일시
    private LocalDateTime createdAt;

    //취소일시
    private LocalDateTime cancelledAt;

    private String ticketCode;

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public Reservation() {
    }

    public Reservation(Performance performance, Seat seat, Member member) {
        this.performance = performance;
        this.seat = seat;
        this.member = member;
        this.reservationCode = UUID.randomUUID().toString(); //예매번호 자동 생성
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public Performance getPerformance() {
        return performance;
    }

    public Seat getSeat() {
        return seat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void cancel() {
        if(this.status == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED);
        }

        this.cancelledAt = LocalDateTime.now();
        this.status = ReservationStatus.CANCELLED;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public boolean isReconfirmed() {
        return reconfirmed;
    }

    public void reconfirm() {
        if(this.status != ReservationStatus.CANCELLED || reconfirmed) {
            throw new CustomException(ErrorCode.RECONFIRM_NOT_ALLOWED);
        }

        this.status = ReservationStatus.CONFIRMED;
        this.reconfirmed = true;
    }
}
