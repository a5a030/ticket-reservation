package com.byunsum.ticket_reservation.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 키워드 통계 응답")
public class KeywordStatsResponse {
    @Schema(description = "키워드", example = "감동")
    private String keyword;

    @Schema(description = "등장 횟수", example = "12")
    private Long count;

    public KeywordStatsResponse(String keyword, Long count) {
        this.keyword = keyword;
        this.count = count;
    }

    public String getKeyword() {
        return keyword;
    }

    public Long getCount() {
        return count;
    }
}
