package com.byunsum.ticket_reservation.performance.domain;

import com.byunsum.ticket_reservation.performance.dto.PerformanceResponse;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDate;
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
    private LocalDate startDate;
    private LocalDate endDate;
    private String time;
    private String genre;
    private String posterUrl;

    //출연진, 예매오픈일, 가격 등 확장 예정


    public Performance() {}

    public Performance(String title, String description, String venue, LocalDate startDate, LocalDate endDate, String time, String genre, String posterUrl) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = time;
        this.genre = genre;
        this.posterUrl = posterUrl;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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
    private List<Seat> seat = new ArrayList<>();

    public List<Seat> getSeat() {
        return seat;
    }
}
