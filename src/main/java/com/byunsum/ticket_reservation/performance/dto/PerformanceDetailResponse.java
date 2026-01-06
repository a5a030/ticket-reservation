package com.byunsum.ticket_reservation.performance.dto;

import com.byunsum.ticket_reservation.performance.domain.PerformanceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PerformanceDetailResponse {
    @Schema(description = "공연 ID", example = "1")
    private Long id;

    @Schema(description = "공연 제목")
    private String title;

    @Schema(description = "공연 설명")
    private String description;

    @Schema(description = "공연장")
    private String venue;

    @Schema(description = "공연 장르")
    private String genre;

    @Schema(description = "포스터 이미지 URL")
    private String posterUrl;

    @Schema(description = "공연 시작일")
    private LocalDate startDate;

    @Schema(description = "공연 종료일")
    private LocalDate endDate;

    @Schema(description = "선예매 오픈일시")
    private LocalDateTime preReservationOpenDateTime;

    @Schema(description = "일반예매 오픈일시")
    private LocalDateTime generalReservationOpenDateTime;

    @Schema(description = "회차별 1인당 최대 예매수")
    private int maxTicketsPerPerson;

    @Schema(description = "예매정책을 위한 분류")
    private PerformanceType type;

    @Schema(description = "공연 회차 목록")
    private List<PerformanceRoundResponse> rounds;

    public PerformanceDetailResponse() {
    }

    public PerformanceDetailResponse(Long id, String title, String description, String venue, String genre, String posterUrl, LocalDate startDate, LocalDate endDate, LocalDateTime preReservationOpenDateTime, LocalDateTime generalReservationOpenDateTime, int maxTicketsPerPerson, PerformanceType type, List<PerformanceRoundResponse> rounds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.preReservationOpenDateTime = preReservationOpenDateTime;
        this.generalReservationOpenDateTime = generalReservationOpenDateTime;
        this.maxTicketsPerPerson = maxTicketsPerPerson;
        this.type = type;
        this.rounds = rounds;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVenue() {
        return venue;
    }

    public String getGenre() {
        return genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDateTime getPreReservationOpenDateTime() {
        return preReservationOpenDateTime;
    }

    public LocalDateTime getGeneralReservationOpenDateTime() {
        return generalReservationOpenDateTime;
    }

    public int getMaxTicketsPerPerson() {
        return maxTicketsPerPerson;
    }

    public PerformanceType getType() {
        return type;
    }

    public List<PerformanceRoundResponse> getRounds() {
        return rounds;
    }
}
