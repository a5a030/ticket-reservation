package com.byunsum.ticket_reservation.reservation.domain.pre;

public enum PreReservationAssignmentStatus {
    ASSIGNED,   // 좌석 배정(결제 대기)
    PAID,       // 결제 완료(좌석 확정)
    CANCELLED,  // 당첨자가 취소
    EXPIRED     // 23:59:59까지 결제 안 해서 당첨건 만료
}
