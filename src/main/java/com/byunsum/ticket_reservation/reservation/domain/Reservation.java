package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

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
        return (int) reservationSeats.stream()
                .filter(rs -> rs.getStatus() == ReservationSeatStatus.HOLD
                || rs.getStatus() == ReservationSeatStatus.CONFIRMED)
                .count();
    }

    @Schema(description = "배송비")
    private int deliveryFee;

    @Schema(description = "배송 방법 (DELIVERY / PICKUP)")
    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Schema(description = "배송 상태 (NONE/READY/SHIPPED/DELIVERED)")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus = DeliveryStatus.NONE;

    public void addSeat(Seat seat, LocalDateTime holdExpiredAt) {
        if(seat.isReserved()) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
        seat.setReserved(true); //좌석 상태 업데이트, HOLD 시점에 이선좌
        this.reservationSeats.add(new ReservationSeat(this, seat, holdExpiredAt));
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        if(this.deliveryMethod == DeliveryMethod.PICKUP && deliveryFee != 0) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_FEE);
        }

        this.deliveryFee = deliveryFee;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;

        if(deliveryMethod == DeliveryMethod.PICKUP) {
            this.deliveryStatus = DeliveryStatus.NONE;
            this.deliveryFee = 0;
        } else {
            if(this.deliveryStatus == DeliveryStatus.NONE) {
                this.deliveryStatus = DeliveryStatus.READY;
            }
        }
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

    public Reservation(Performance performance, Member member) {
        this();
        this.performance = performance;
        this.member = member;
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

    public void cancelAll() {
        if(this.status == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED);
        }

        LocalDateTime now = LocalDateTime.now();

        for(ReservationSeat seat : reservationSeats) {
            seat.cancel(0,0,now);
        }

        this.cancelledAt = now;
        this.status = ReservationStatus.CANCELLED;
    }

    public void cancelSeats(List<Long> seatIds, int cancelFee, int refundAmount, LocalDateTime now) {
        if(seatIds == null || seatIds.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        long matched = reservationSeats.stream()
                .filter(rs -> seatIds.contains(rs.getSeat().getId()))
                .count();

        if(matched != seatIds.size()) {
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }

        long cancelCount = 0;

        for(ReservationSeat rs : reservationSeats) {
            if(!seatIds.contains(rs.getSeat().getId())) continue;

            if(rs.getStatus() == ReservationSeatStatus.CANCELLED || rs.getStatus() == ReservationSeatStatus.RELEASED) {
                continue;
            }

            rs.cancel(cancelFee,refundAmount,now);
            cancelCount++;
        }

        if(cancelCount == 0) {
            throw new CustomException(ErrorCode.INVALID_SEAT_STATUS);
        }

        if(isAllInactive()) {
            this.cancelledAt = now;
            this.status = ReservationStatus.CANCELLED;
        }
    }

    private boolean isAllInactive() {
        return reservationSeats.stream()
                .allMatch(seat -> seat.getStatus() == ReservationSeatStatus.CANCELLED
                        || seat.getStatus() == ReservationSeatStatus.RELEASED);
    }

    public void confirmAllSeats(LocalDateTime now) {
        boolean hasInactive = reservationSeats.stream()
                .anyMatch(rs -> rs.getStatus() == ReservationSeatStatus.RELEASED
                || rs.getStatus() == ReservationSeatStatus.CANCELLED);

        if(hasInactive) {
            throw new CustomException(ErrorCode.INVALID_SEAT_STATUS);
        }

        for(ReservationSeat seat : reservationSeats) {
            seat.confirm(now);
        }

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
        return reservationSeats.stream()
                .map(ReservationSeat::getSeat)
                .toList();
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
        long activeCount = reservationSeats.stream()
                .filter(rs -> rs.getStatus() == ReservationSeatStatus.HOLD
                        || rs.getStatus() == ReservationSeatStatus.CONFIRMED)
                .count();

        int seatTotal = reservationSeats.stream()
                .filter(seat -> seat.getStatus() == ReservationSeatStatus.HOLD
                        || seat.getStatus() == ReservationSeatStatus.CONFIRMED)
                .mapToInt(ReservationSeat::getPriceAtReservation)
                .sum();

        int bookingFee = (int) (2000 * activeCount);

        return seatTotal + bookingFee + deliveryFee;
    }

    public List<ReservationSeat> getReservationSeats() {
        return reservationSeats;
    }

    public boolean isShipped() {
        return deliveryStatus == DeliveryStatus.SHIPPED || deliveryStatus == DeliveryStatus.DELIVERED;
    }

    public boolean isDelivered() {
        return deliveryStatus == DeliveryStatus.DELIVERED;
    }

    public void markAsShipped() {
        if(this.deliveryMethod == null || this.deliveryMethod != DeliveryMethod.DELIVERY) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_METHOD);
        }

        if(this.deliveryStatus != DeliveryStatus.READY) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_STATUS);
        }

        this.deliveryStatus = DeliveryStatus.SHIPPED;
    }

    public void markAsDelivered() {
        if(this.deliveryMethod == null || this.deliveryMethod != DeliveryMethod.DELIVERY) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_METHOD);
        }

        if(this.deliveryStatus != DeliveryStatus.SHIPPED) {
            throw new CustomException(ErrorCode.INVALID_DELIVERY_STATUS);
        }
        this.deliveryStatus = DeliveryStatus.DELIVERED;
    }

    public boolean isPartiallyCancelled() {
        boolean hasCancelled = reservationSeats.stream().anyMatch(s -> s.getStatus() == ReservationSeatStatus.CANCELLED);
        boolean hasConfirmed = reservationSeats.stream().anyMatch(s -> s.getStatus() == ReservationSeatStatus.CONFIRMED);

        return hasCancelled && hasConfirmed;
    }
}
