package com.byunsum.ticket_reservation.ticket.domain;

public enum ReissueReason {
    USER_REQUEST,      // 고객 요청(분실/변심/요청)
    QR_ERROR,          // QR 생성 오류/깨짐
    PAYMENT_ISSUE,     // 결제/정합성 이슈
    ADMIN_MANUAL,      // 관리자 수동 재발급
    SECURITY           // 보안 의심(도용/유출)
}
