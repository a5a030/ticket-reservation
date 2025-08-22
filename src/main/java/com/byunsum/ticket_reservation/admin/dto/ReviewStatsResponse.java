package com.byunsum.ticket_reservation.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ReviewStatsResponse(@Schema(description = "리뷰 총 개수")
                                  long totalReviews,

                                  @Schema(description = "긍정/부정 비율")
                                  Map<String, Long> sentimentCount,

                                  @Schema(description = "자주 언급된 키워드")
                                  Map<String, Long> topKeywords) {
}
