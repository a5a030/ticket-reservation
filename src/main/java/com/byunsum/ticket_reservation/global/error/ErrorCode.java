package com.byunsum.ticket_reservation.global.error;

public enum ErrorCode {
    UNAUTHORIZED("인증이 필요합니다."),
    FORBIDDEN("접근 권한이 없습니다."),

    INVALID_INPUT_VALUE("잘못된 입력입니다."),
    METHOD_NOT_ALLOWED("허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다."),

    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다."),
    PERFORMANCE_NOT_FOUND("공연 정보를 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND("예매 정보를 찾을 수 없습니다."),
    DUPLICATE_SEAT("이미 예매된 좌석입니다."),
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
