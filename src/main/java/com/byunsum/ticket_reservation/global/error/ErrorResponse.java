package com.byunsum.ticket_reservation.global.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답 포맷")
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "RESERVATION_NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "예매 정보를 찾을 수 없습니다.")
    private String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
