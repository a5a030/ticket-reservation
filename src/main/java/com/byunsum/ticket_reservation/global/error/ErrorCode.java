package com.byunsum.ticket_reservation.global.error;


public enum ErrorCode {
    UNAUTHORIZED("인증이 필요합니다."),
    FORBIDDEN("접근 권한이 없습니다."),

    INVALID_INPUT_VALUE("잘못된 입력입니다."),
    METHOD_NOT_ALLOWED("허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다."),

    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다."),
    DUPLICATE_MEMBER("이미 존재하는 회원입니다."),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다."),

    PERFORMANCE_NOT_FOUND("공연 정보를 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND("예매 정보를 찾을 수 없습니다."),
    SEAT_ALREADY_RESERVED("이미 예매된 좌석입니다."),
    SEAT_ALREADY_SELECTED("이미 선택된 좌석입니다."),
    SEAT_ALREADY_RELEASED("좌석이 이미 풀려 재확정할 수 없습니다."),
    SEAT_NOT_FOUND("좌석을 찾을 수 없습니다."),
    ALREADY_CANCELED("이미 취소된 예매입니다."),
    RECONFIRM_NOT_ALLOWED("해당 예매는 재확정할 수 없습니다"),
    ALREADY_CANCELED_PAYMENT("이미 취소된 결제입니다."),
    UNAUTHORIZED_CANCEL("본인의 예매만 취소할 수 있습니다."),
    PAYMENT_NOT_FOUND("결제 내역을 찾을 수 없습니다."),

    TICKET_ALREADY_ISSUED("티켓이 이미 발급되었습니다."),
    TICKET_NOT_FOUND("해당 티켓을 찾을 수 없습니다."),
    QR_NOT_YET_AVAILABLE("QR 코드는 공연 시작 3시간 전부터 발급할 수 있습니다."),
    QR_ALREADY_USED("이미 사용된 QR 코드입니다."),
    QR_EXPIRED("QR 코드가 만료되었습니다."),
    QR_INVALID("유효하지 않은 QR 코드입니다."),
    QR_UNAUTHORIZED("해당 QR 코드에 대한 권한이 없습니다."),

    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다."),

    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_REVIEW_ACCESS("본인의 리뷰만 수정/삭제할 수 있습니다."),

    RATE_LIMIT_EXCEEDED("요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    INVALID_SENTIMENT("해당 감정 유형은 지원하지 않습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
