package com.byunsum.ticket_reservation.ticket.domain;

public enum TicketStatus {
    RESERVED,       // 좌석 선택 후 예매 보류
    PAID,           // 결제 완료 (티켓 발급 전)
    ISSUED,         // 티켓 발급 (QR 생성)
    USED,           // 검표 완료
    CANCELLED,       // 사용자 취소
    REFUNDED,       // 결제 취소/환불 완료
    INVALIDATED,    // 재발급으로 무효 처리
    EXPIRED;        // TTL 만료 (좌석 자동 해제)

    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case RESERVED -> target == PAID || target == EXPIRED;
            case PAID -> target == ISSUED || target == REFUNDED;
            case ISSUED -> target == USED || target == CANCELLED || target == INVALIDATED;
            case USED, CANCELLED, REFUNDED, INVALIDATED, EXPIRED -> false;
        };
    }
}
