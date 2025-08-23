package com.byunsum.ticket_reservation.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerformanceRequest {
    @Schema(description = "공연 제목")
    private String title;

    @Schema(description = "공연 설명")
    private String description;

    @Schema(description = "공연 장르")
    private String genre;

    @Schema(description = "포스터 이미지 URL")
    private String posterUrl;

    @Schema(description = "공연장")
    private String venue;

    @Schema(description = "공연 시간")
    private String time;

    @Schema(description = "공연 시작일")
    private LocalDate startDate;

    @Schema(description = "공연 종료일")
    private LocalDate endDate;

    @Schema(description = "선예매 오픈")
    private LocalDateTime preReservationOpenDate;

    @Schema(description = "일반예매 오픈")
    private LocalDateTime generalOpenDate;

    @Schema(description = "회차당 1인 최대 예매 수량")
    private int maxTicketsPerPerson;

    public PerformanceRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate starDate) {
        this.startDate = starDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getPreReservationOpenDate() {
        return preReservationOpenDate;
    }

    public void setPreReservationOpenDate(LocalDateTime preReservationOpenDate) {
        this.preReservationOpenDate = preReservationOpenDate;
    }

    public LocalDateTime getGeneralOpenDate() {
        return generalOpenDate;
    }

    public void setGeneralOpenDate(LocalDateTime generalOpenDate) {
        this.generalOpenDate = generalOpenDate;
    }

    public int getMaxTicketsPerPerson() {
        return maxTicketsPerPerson;
    }

    public void setMaxTicketPerPerson(int maxTicketsPerPerson) {
        this.maxTicketsPerPerson = maxTicketsPerPerson;
    }
}
