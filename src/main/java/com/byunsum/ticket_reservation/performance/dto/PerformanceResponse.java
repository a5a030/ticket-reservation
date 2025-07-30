package com.byunsum.ticket_reservation.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class PerformanceResponse {
    @Schema(description = "공연 ID", example = "1")
    private Long id;

    @Schema(description = "공연 제목")
    private String title;

    @Schema(description = "공연 설명")
    private String description;

    @Schema(description = "공연장")
    private String venue;

    @Schema(description = "공연 시작일")
    private LocalDate startDate;

    @Schema(description = "공연 종료일")
    private LocalDate endDate;

    @Schema(description = "공연 시간")
    private String time;

    @Schema(description = "공연 장르")
    private String genre;

    @Schema(description = "포스터 이미지 URL")
    private String posterUrl;

    public PerformanceResponse() {
    }

    public PerformanceResponse(Long id, String title, String description, String venue, LocalDate startDate, LocalDate endDate, String time, String genre, String posterUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = time;
        this.genre = genre;
        this.posterUrl = posterUrl;
    }

    //불변객체
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getTime() {
        return time;
    }

    public String getGenre() {
        return genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
