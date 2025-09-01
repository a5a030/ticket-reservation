package com.byunsum.ticket_reservation.performance.domain;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime time;
    private String genre;
    private String posterUrl;
    private LocalDateTime preReservationOpenDateTime;
    private LocalDateTime generalReservationOpenDateTime;
    private int maxTicketsPerPerson;

    @Enumerated(EnumType.STRING)
    private PerformanceType type;

    //출연진, 가격 등 확장 예정


    public Performance() {}

    public Performance(String title, String description, String venue, LocalDate startDate, LocalDate endDate, LocalTime time, String genre, String posterUrl, LocalDateTime preReservationOpenDateTime, LocalDateTime generalReservationOpenDateTime, int maxTicketsPerPerson, PerformanceType type) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = time;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.preReservationOpenDateTime = preReservationOpenDateTime;
        this.generalReservationOpenDateTime = generalReservationOpenDateTime;
        this.maxTicketsPerPerson = maxTicketsPerPerson;
        this.type = type;
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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
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

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    public List<Seat> getSeats() {
        return seats;
    }

    public LocalDateTime getStartTime() {
        return LocalDateTime.of(this.startDate, this.time);
    }

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public List<Reservation> getReservations() {
        return reservations;
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

    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(this.startDate, this.time);
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
}
