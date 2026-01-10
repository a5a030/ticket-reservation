package com.byunsum.ticket_reservation.ticket.domain;

public enum TicketStatus {
    ISSUED,         // 티켓 발급 (QR 생성)
    USED,           // 검표 완료
    INVALIDATED,    // 재발급으로 무효 처리
    EXPIRED;        // 티켓 만료(유효기간 초과/정책 만료)

    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case ISSUED -> target == USED || target == INVALIDATED || target == EXPIRED;
            case USED, INVALIDATED, EXPIRED -> false;
        };
    }
}
