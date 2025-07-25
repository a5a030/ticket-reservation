package com.byunsum.ticket_reservation.performance.dto;

import java.time.LocalDate;

public class PerformanceResponse {
    private Long id;
    private String title;
    private String description;
    private String venue;
    private LocalDate startDate;
    private LocalDate endDate;
    private String time;
    private String genre;
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
