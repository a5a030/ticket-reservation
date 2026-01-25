package com.byunsum.ticket_reservation.performance.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservationType;
import com.byunsum.ticket_reservation.reservation.domain.sale.ReleaseTarget;
import com.byunsum.ticket_reservation.reservation.domain.sale.SalePhase;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String venue; //공연장
    private String genre;
    private String posterUrl;
    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime preReservationOpenDateTime;
    private LocalDateTime generalReservationOpenDateTime;

    private int maxTicketsPerPerson;

    @Enumerated(EnumType.STRING)
    private PerformanceType type;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceRound> rounds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreReservationType preReservationType = PreReservationType.PRE_SALE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalePhase salePhase = SalePhase.GENERAL_SALE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReleaseTarget releaseTarget = ReleaseTarget.GENERAL_SALE;

    //출연진, 가격 등 확장 예정


    public Performance() {}

    public Performance(String title, String description, String venue, String genre, String posterUrl, LocalDate startDate, LocalDate endDate, LocalDateTime preReservationOpenDateTime, LocalDateTime generalReservationOpenDateTime, int maxTicketsPerPerson, PerformanceType type) {
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
    }

    public void addRound(PerformanceRound round) {
        if(round == null) return;
        if(round.getPerformance() != null && round.getPerformance() != this) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if(!this.rounds.contains(round)) {
            this.rounds.add(round);
        }

        round.setPerformance(this);
    }

    public void removeRound(PerformanceRound round) {
        if(round == null) return;
        this.rounds.remove(round);

        if(round.getPerformance() == this) {
            round.setPerformance(null);
        }
    }

    public Long getId() {
        return id;
    }

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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
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

    public LocalDateTime getGeneralReservationOpenDateTime() {
        return generalReservationOpenDateTime;
    }

    public void setGeneralReservationOpenDateTime(LocalDateTime generalReservationOpenDateTime) {
        this.generalReservationOpenDateTime = generalReservationOpenDateTime;
    }

    public void setPreReservationOpenDateTime(LocalDateTime preReservationOpenDateTime) {
        this.preReservationOpenDateTime = preReservationOpenDateTime;
    }

    public int getMaxTicketsPerPerson() {
        return maxTicketsPerPerson;
    }

    public void setMaxTicketsPerPerson(int maxTicketsPerPerson) {
        this.maxTicketsPerPerson = maxTicketsPerPerson;
    }

    public PerformanceType getType() {
        return type;
    }

    public void setType(PerformanceType type) {
        this.type = type;
    }

    public List<PerformanceRound> getRounds() {
        return Collections.unmodifiableList(rounds);
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

    public boolean isPreSalePolicy() {
        return this.preReservationType == PreReservationType.PRE_SALE;
    }

    public boolean isSeatAssignmentPolicy() {
        return this.preReservationType == PreReservationType.SEAT_ASSIGNMENT;
    }

    public boolean isDrawPayPhase() {
        return this.salePhase == SalePhase.DRAW_PAY;
    }

    public boolean isPreSalePhase() {
        return this.salePhase == SalePhase.PRE_SALE;
    }

    public boolean isGeneralPhase() {
        return this.salePhase == SalePhase.GENERAL_SALE;
    }

    public void updateFrom(PerformanceRequest request) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.venue = request.getVenue();
        this.genre = request.getGenre();
        this.posterUrl = request.getPosterUrl();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.preReservationOpenDateTime = request.getPreReservationOpenDateTime();
        this.generalReservationOpenDateTime = request.getGeneralReservationOpenDateTime();
        this.maxTicketsPerPerson = request.getMaxTicketsPerPerson();
        this.type = request.getType();
        this.preReservationType = request.getPreReservationType();
        this.salePhase = request.getSalePhase();
        this.releaseTarget = request.getReleaseTarget();

        validatePolicy();

    }

    public void validatePolicy() {
        if(preReservationType == PreReservationType.SEAT_ASSIGNMENT && salePhase == SalePhase.PRE_SALE) {
            throw new CustomException(ErrorCode.INVALID_SALE_POLICY);
        }

        if(preReservationType == PreReservationType.PRE_SALE && salePhase == SalePhase.DRAW_PAY) {
            throw new CustomException(ErrorCode.INVALID_SALE_POLICY);
        }
    }
}
