package com.byunsum.ticket_reservation.performance.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PerformanceRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Performance performance;

    private LocalDateTime date;
    private String startTime;
    private int roundNumber;

    public PerformanceRound() {
    }

    public PerformanceRound(Performance performance, LocalDateTime date, String startTime, int roundNumber) {
        this.performance = performance;
        this.date = date;
        this.startTime = startTime;
        this.roundNumber = roundNumber;
    }

    public Long getId() {
        return id;
    }

    public Performance getPerformance() {
        return performance;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
