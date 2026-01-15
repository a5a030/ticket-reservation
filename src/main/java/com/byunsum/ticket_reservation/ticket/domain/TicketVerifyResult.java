package com.byunsum.ticket_reservation.ticket.domain;

public enum TicketVerifyResult {
    SUCCESS,            // 정상 검표 성공 (입장 완료)
    DUPLICATE_SCAN,     // 동시/중복 스캔 시도
    USED,               // 이미 사용된 티켓
    EXPIRED,            // 만료된 티켓
    INVALIDATED,        // 재발급 등으로 무효 처리된 티켓
    TAMPERED,           // 위변조 / 매핑 불일치 의심
    NOT_FOUND,          // 티켓 코드 존재하지 않음
    INVALID_STATE;      // 예상치 못한 티켓 상태

    public static TicketVerifyResult fromTicketStatus(TicketStatus status) {
        return switch (status) {
            case USED -> USED;
            case INVALIDATED -> INVALIDATED;
            case EXPIRED -> EXPIRED;
            case TAMPERED -> TAMPERED;
            case ISSUED ->  SUCCESS;
        };
    }
}
