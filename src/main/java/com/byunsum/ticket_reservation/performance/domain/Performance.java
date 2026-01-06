package com.byunsum.ticket_reservation.performance.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private LocalDateTime preReservationOpenDateTime;
    private LocalDateTime generalReservationOpenDateTime;
    private int maxTicketsPerPerson;

    @Enumerated(EnumType.STRING)
    private PerformanceType type;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceRound> rounds = new ArrayList<>();

    //출연진, 가격 등 확장 예정


    public Performance() {}

    public Performance(String title, String description, String venue, String genre, String posterUrl, LocalDateTime preReservationOpenDateTime, LocalDateTime generalReservationOpenDateTime, int maxTicketsPerPerson, PerformanceType type) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.preReservationOpenDateTime = preReservationOpenDateTime;
        this.generalReservationOpenDateTime = generalReservationOpenDateTime;
        this.maxTicketsPerPerson = maxTicketsPerPerson;
        this.type = type;
    }

    public void addRound(PerformanceRound round) {
        this.rounds.add(round);
        round.setPerformance(this);
    }

    public void removeRound(PerformanceRound round) {
        this.rounds.remove(round);
        round.setPerformance(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return rounds;
    }
}
