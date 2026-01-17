package com.byunsum.ticket_reservation.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //공통/시스템
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    //회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    //공연
    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "공연 정보를 찾을 수 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "공연 시작일은 종료일보다 늦을 수 없습니다."),
    INVALID_RESERVATION_OPEN_TIME(HttpStatus.BAD_REQUEST, "선예매 오픈일시는 일반 예매 오픈일시보다 늦을 수 없습니다."),
    INVALID_SORT(HttpStatus.BAD_REQUEST, "유효하지 않은 정렬 조건입니다."),

    //회차/선예매
    ROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "회차 정보를 찾을 수 없습니다."),
    DUPLICATE_PRE_RESERVATION(HttpStatus.CONFLICT, "이미 선예매에 응모하셨습니다."),
    NO_PRE_RESERVATION_APPLICANTS(HttpStatus.NOT_FOUND, "해당 회차의 선예매 응모자가 없습니다."),
    PRE_RESERVATION_REQUIRED(HttpStatus.FORBIDDEN, "선예매 대상자만 예매할 수 있습니다."),
    NOT_PRE_RESERVATION_WINNER(HttpStatus.FORBIDDEN, "선예매 당첨자가 아닙니다."),
    RESERVATION_NOT_OPEN(HttpStatus.BAD_REQUEST, "아직 예매가 오픈되지 않았습니다."),

    //예매/좌석
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예매 정보를 찾을 수 없습니다."),
    EXCEED_MAX_TICKETS(HttpStatus.BAD_REQUEST, "회차당 1인 최대 예매 수량을 초과했습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "이미 예매된 좌석입니다."),
    SEAT_ALREADY_SELECTED(HttpStatus.CONFLICT, "이미 선택된 좌석입니다."),
    SEAT_ALREADY_RELEASED(HttpStatus.BAD_REQUEST, "좌석이 이미 풀려 재확정할 수 없습니다."),
    BOOKING_CLOSED(HttpStatus.BAD_REQUEST, "예매 가능 시간이 지났습니다."),
    ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 예매입니다."),
    UNAUTHORIZED_CANCEL(HttpStatus.FORBIDDEN, "본인의 예매만 취소할 수 있습니다."),
    INVALID_SEAT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 좌석 상태입니다."),
    SEAT_HOLD_EXPIRED(HttpStatus.BAD_REQUEST, "좌석 홀딩 시간이 만료되었습니다."),
    INVALID_RELEASE_REQUEST(HttpStatus.BAD_REQUEST, "좌석 해제 요청이 유효하지 않습니다."),
    INVALID_SEAT_SELECTION(HttpStatus.BAD_REQUEST, "선택되지 않았거나 본인이 선택한 좌석이 아닙니다."),


    //재확정
    INVALID_RECONFIRM_STATUS(HttpStatus.BAD_REQUEST, "재확정 가능한 상태가 아닙니다."),
    ALREADY_RECONFIRMED(HttpStatus.CONFLICT, "이미 재확정된 예매입니다."),
    UNAUTHORIZED_RECONFIRM(HttpStatus.FORBIDDEN, "본인의 예매만 재확정할 수 있습니다."),
    RECONFIRM_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "해당 예매는 재확정할 수 없습니다"),
    RECONFIRM_EXPIRED(HttpStatus.BAD_REQUEST, "재확정 가능 시간이 지났습니다."),

    //결제
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 내역을 찾을 수 없습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "잘못된 취소 금액입니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 상태입니다."),
    ALREADY_CANCELED_PAYMENT(HttpStatus.CONFLICT, "이미 취소된 결제입니다."),
    CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "공연 당일 및 이후에는 예매 취소가 불가능합니다."),

    //배송
    DELIVERY_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "배송이 시작된 티켓은 직접 취소할 수 없습니다."),
    INVALID_DELIVERY_METHOD(HttpStatus.BAD_REQUEST, "유효하지 않은 배송 방식입니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 배송 상태 전이입니다."),
    INVALID_DELIVERY_FEE(HttpStatus.BAD_REQUEST, "배송 방식에 맞지 않는 배송비입니다."),
    DELIVERY_IN_PROGRESS(HttpStatus.BAD_REQUEST, "배송이 시작된 예매는 고객센터를 통해 취소할 수 있습니다."),
    DELIVERY_COMPLETED(HttpStatus.BAD_REQUEST, "배송 완료된 예매는 고객센터를 통해 취소할 수 있습니다."),
    MANUAL_REFUND_REQUIRED(HttpStatus.BAD_REQUEST, "해당 예매는 고객센터를 통해 환불 처리됩니다."),

    //티켓/QR
    TICKET_ALREADY_ISSUED(HttpStatus.CONFLICT, "티켓이 이미 발급되었습니다."),
    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 티켓을 찾을 수 없습니다."),
    QR_NOT_YET_AVAILABLE(HttpStatus.BAD_REQUEST, "QR 코드는 공연 시작 3시간 전부터 발급할 수 있습니다."),
    QR_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용된 QR 코드입니다."),
    QR_EXPIRED(HttpStatus.BAD_REQUEST, "QR 코드가 만료되었습니다."),
    QR_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 QR 코드입니다."),
    QR_UNAUTHORIZED(HttpStatus.FORBIDDEN, "해당 QR 코드에 대한 권한이 없습니다."),

    //큐/대기열
    QUEUE_NOT_FOUND(HttpStatus.BAD_REQUEST, "대기열에 등록되지 않았습니다."),
    QUEUE_EXPIRED(HttpStatus.BAD_REQUEST, "대기열 토큰이 만료되었습니다."),

    //알림
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),

    //리뷰
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN, "본인의 리뷰만 수정/삭제할 수 있습니다."),
    INVALID_SENTIMENT(HttpStatus.BAD_REQUEST, "해당 감정 유형은 지원하지 않습니다."),

    //외부 연동
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
