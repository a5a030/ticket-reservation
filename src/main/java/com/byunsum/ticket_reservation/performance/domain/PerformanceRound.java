package com.byunsum.ticket_reservation.performance.domain;

import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "performanceRound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

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
    public List<Seat> getSeats() {
        return seats;
    }
}
