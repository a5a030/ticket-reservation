package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeat> seats = new ArrayList<>();

    public boolean isCancelled() {
        return this.status == ReservationStatus.CANCELLED;
    }

    private boolean reconfirmed = false; // 재확정 1회 한정 허용

    @Schema(description = "예매일시")
    private LocalDateTime createdAt;

    @Schema(description = "취소일시")
    private LocalDateTime cancelledAt;

    private String ticketCode;

    @Schema(description = "예매수량")
    public int getQuantity() {
        return seats.size();
    }

    @Schema(description = "배송비")
    private int deliveryFee;

    @Schema(description = "배송 방법 (DELIVERY / PICKUP)")
    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Schema(description = "배송시작여부")
    private boolean shipped = false;

    public void addSeat(Seat seat) {
        if(seat.isReserved()) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
        seat.setReserved(true); //좌석 상태 업데이트
        ReservationSeat rs = new ReservationSeat(this, seat);
        this.seats.add(rs);
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

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
        this.reservationCode = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
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

        for(ReservationSeat seat : seats) {
            seat.cancel();
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

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public List<Seat> getSeats() {
        return seats.stream()
                .map(ReservationSeat::getSeat)
                .toList();
    }

    public void addReservationSeat(ReservationSeat rs) {
        this.seats.add(rs);
    }

    public boolean isReconfirmed() {
        return reconfirmed;
    }

    public void reconfirm() {
        if(this.status != ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.INVALID_RECONFIRM_STATUS);
        }

        if(this.reconfirmed) {
            throw new CustomException(ErrorCode.ALREADY_RECONFIRMED);
        }

        this.status = ReservationStatus.CONFIRMED;
        this.reconfirmed = true;
    }

    public int calculateTotalAmount() {
        int seatTotal = getSeats().stream()
                .mapToInt(Seat::getPrice)
                .sum();

        int bookingFee = 2000 * getQuantity();

        return seatTotal + bookingFee + deliveryFee;
    }

    public List<ReservationSeat> getReservationSeats() {
        return seats;
    }

    public boolean isShipped() {
        return shipped;
    }

    public void markAsShipped() {
        this.shipped = true;
    }
}
