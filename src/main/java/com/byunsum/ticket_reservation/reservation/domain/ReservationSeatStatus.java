package com.byunsum.ticket_reservation.reservation.domain;

import java.util.EnumSet;

public enum ReservationSeatStatus {
    HOLD,           // 결제 전 좌석 점유(이선좌 표시)
    CONFIRMED,      // 결제된 좌석으로 취급(카드결제 + 계좌이체 예매완료)
    CANCELLED,      // 사용자/관리자 취소(환불/취소수수료 이력 필요)
    RELEASED;       // 시스템 자동 해제(TTL 만료, 입금 마감 미이행0

    public boolean canTransitionTo(ReservationSeatStatus next) {
        return switch (this) {
            case HOLD -> next == CONFIRMED || next == CANCELLED || next == RELEASED;
            case CONFIRMED ->  next == RELEASED || next == CANCELLED;
            case CANCELLED -> false;
            case RELEASED -> false;
        };
    }

    public void assertTransitionTo(ReservationSeatStatus next) {
        if(!canTransitionTo(next)) {
            throw new IllegalStateException("Invalid transition: "+this+" -> "+next);
        }
    }
}
