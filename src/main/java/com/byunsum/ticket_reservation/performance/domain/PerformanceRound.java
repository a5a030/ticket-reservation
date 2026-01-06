package com.byunsum.ticket_reservation.performance.domain;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
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
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Column(nullable = false)
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime entryDateTime;

    @Column(nullable = false)
    private int roundNumber;

    @OneToMany(mappedBy = "performanceRound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public PerformanceRound() {
    }

    public PerformanceRound(Performance performance, LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime entryDateTime, int roundNumber) {
        this.performance = performance;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.entryDateTime = entryDateTime;
        this.roundNumber = roundNumber;
    }

    public Long getId() {
        return id;
    }

    public Performance getPerformance() {
        return performance;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public LocalDateTime getEndDateTime() {return endDateTime;}
    public LocalDateTime getEntryDateTime() {return entryDateTime;}

    public int getRoundNumber() {
        return roundNumber;
    }
    public List<Seat> getSeats() {
        return seats;
    }

    @OneToMany(mappedBy = "performanceRound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }
}
