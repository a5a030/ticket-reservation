package com.byunsum.ticket_reservation.performance.dto;

import com.byunsum.ticket_reservation.performance.domain.PerformanceType;
import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservationType;
import com.byunsum.ticket_reservation.reservation.domain.sale.ReleaseTarget;
import com.byunsum.ticket_reservation.reservation.domain.sale.SalePhase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PerformanceRequest {
    @NotBlank
    @Schema(description = "공연 제목")
    private String title;

    @Schema(description = "공연 설명")
    private String description;

    @NotBlank
    @Schema(description = "공연 장르")
    private String genre;

    @Schema(description = "포스터 이미지 URL")
    private String posterUrl;

    @NotBlank
    @Schema(description = "공연장")
    private String venue;

    @NotNull
    @Schema(description = "공연 시작일")
    private LocalDate startDate;

    @NotNull
    @Schema(description = "공연 종료일")
    private LocalDate endDate;

    @Schema(description = "선예매 오픈일시")
    private LocalDateTime preReservationOpenDateTime;

    @Schema(description = "일반예매 오픈일시")
    private LocalDateTime generalReservationOpenDateTime;

    @NotNull
    @Min(1)
    @Schema(description = "회차 기준 1인당 최대 예매 수량")
    private Integer maxTicketsPerPerson;

    @NotNull
    @Schema(description = "예매정책을 위한 분류")
    private PerformanceType type;

    @NotNull
    @Schema(description = "선예매 정책 (PRE_SALE / SEAT_ASSIGNMENT)")
    private PreReservationType preReservationType;

    @NotNull
    @Schema(description = "판매 단계 (DRAW_PAY / PRE_SALE / GENERAL_SALE)")
    private SalePhase salePhase;

    @NotNull
    @Schema(description = "취소표/잔여석 공개 대상 (PRE_SALE / GENERAL_SALE)")
    private ReleaseTarget releaseTarget;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getPreReservationOpenDateTime() {
        return preReservationOpenDateTime;
    }

    public void setPreReservationOpenDateTime(LocalDateTime preReservationOpenDateTime) {
        this.preReservationOpenDateTime = preReservationOpenDateTime;
    }

    public LocalDateTime getGeneralReservationOpenDateTime() {
        return generalReservationOpenDateTime;
    }

    public void setGeneralReservationOpenDateTime(LocalDateTime generalReservationOpenDateTime) {
        this.generalReservationOpenDateTime = generalReservationOpenDateTime;
    }

    public Integer getMaxTicketsPerPerson() {
        return maxTicketsPerPerson;
    }

    public void setMaxTicketsPerPerson(Integer maxTicketsPerPerson) {
        this.maxTicketsPerPerson = maxTicketsPerPerson;
    }

    public PerformanceType getType() {
        return type;
    }

    public void setType(PerformanceType type) {
        this.type = type;
    }

    public PreReservationType getPreReservationType() {
        return preReservationType;
    }

    public void setPreReservationType(PreReservationType preReservationType) {
        this.preReservationType = preReservationType;
    }

    public SalePhase getSalePhase() {
        return salePhase;
    }

    public void setSalePhase(SalePhase salePhase) {
        this.salePhase = salePhase;
    }

    public ReleaseTarget getReleaseTarget() {
        return releaseTarget;
    }

    public void setReleaseTarget(ReleaseTarget releaseTarget) {
        this.releaseTarget = releaseTarget;
    }
}
