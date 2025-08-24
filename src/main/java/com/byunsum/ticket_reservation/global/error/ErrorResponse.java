package com.byunsum.ticket_reservation.global.error;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "에러 응답 포맷")
public class ErrorResponse {
    @Schema(description = "HTTP 상태 코드", example = "404")
    private int status;

    @Schema(description = "에러 코드", example = "RESERVATION_NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "예매 정보를 찾을 수 없습니다.")
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.status = errorCode.getStatus().value();
        this.code = errorCode.name();
        this.message = customMessage;
    }

    public ErrorResponse(HttpStatus status, String code, String message) {
        this.status = status.value();
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
