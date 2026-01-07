package com.byunsum.ticket_reservation.global.error;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(description = "에러 응답 포맷")
public class ErrorResponse {
    @Schema(description = "HTTP 상태 코드", example = "404")
    private int status;

    @Schema(description = "에러 코드", example = "RESERVATION_NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "예매 정보를 찾을 수 없습니다.")
    private String message;

    @Schema(description = "에러 발생 시각")
    private LocalDateTime timestamp;

    public ErrorResponse(ErrorCode errorCode) {
        init(errorCode.getStatus().value(), errorCode.name(), errorCode.getMessage());
    }

    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        init(errorCode.getStatus().value(), errorCode.name(), customMessage);
    }

    public ErrorResponse(HttpStatus status, String code, String message) {
        init(status.value(), code, message);
    }

    private void init(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
