package com.byunsum.ticket_reservation.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    DUPLICATE_PRE_RESERVATION(HttpStatus.CONFLICT, "이미 선예매에 응모하셨습니다."),
    ROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "회차 정보를 찾을 수 없습니다."),
    NO_PRE_RESERVATION_APPLICANTS(HttpStatus.NOT_FOUND, "해당 회차의 선예매 응모자가 없습니다."),
    PRE_RESERVATION_REQUIRED(HttpStatus.FORBIDDEN, "선예매 대상자만 예매할 수 있습니다."),
    NOT_PRE_RESERVATION_WINNER(HttpStatus.FORBIDDEN, "선예매 당첨자가 아닙니다."),
    RESERVATION_NOT_OPEN(HttpStatus.BAD_REQUEST, "아직 예매가 오픈되지 않았습니다."),
    EXCEED_MAX_TICKETS(HttpStatus.BAD_REQUEST, "회차당 1인 최대 예매 수량을 초과했습니다."),

    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "공연 정보를 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예매 정보를 찾을 수 없습니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "이미 예매된 좌석입니다."),
    SEAT_ALREADY_SELECTED(HttpStatus.CONFLICT, "이미 선택된 좌석입니다."),
    SEAT_ALREADY_RELEASED(HttpStatus.BAD_REQUEST, "좌석이 이미 풀려 재확정할 수 없습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."),
    ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 예매입니다."),
    RECONFIRM_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "해당 예매는 재확정할 수 없습니다"),
    ALREADY_CANCELED_PAYMENT(HttpStatus.CONFLICT, "이미 취소된 결제입니다."),
    UNAUTHORIZED_CANCEL(HttpStatus.FORBIDDEN, "본인의 예매만 취소할 수 있습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 내역을 찾을 수 없습니다."),

    TICKET_ALREADY_ISSUED(HttpStatus.CONFLICT, "티켓이 이미 발급되었습니다."),
    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 티켓을 찾을 수 없습니다."),
    QR_NOT_YET_AVAILABLE(HttpStatus.BAD_REQUEST, "QR 코드는 공연 시작 3시간 전부터 발급할 수 있습니다."),
    QR_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용된 QR 코드입니다."),
    QR_EXPIRED(HttpStatus.BAD_REQUEST, "QR 코드가 만료되었습니다."),
    QR_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 QR 코드입니다."),
    QR_UNAUTHORIZED(HttpStatus.FORBIDDEN, "해당 QR 코드에 대한 권한이 없습니다."),

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN, "본인의 리뷰만 수정/삭제할 수 있습니다."),
    INVALID_SENTIMENT(HttpStatus.BAD_REQUEST, "해당 감정 유형은 지원하지 않습니다."),

    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "외부 API 호출 중 오류가 발생했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
