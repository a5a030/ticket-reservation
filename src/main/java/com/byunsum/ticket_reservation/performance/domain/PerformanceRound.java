package com.byunsum.ticket_reservation.performance.domain;

import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_round_performance_round_number",
                columnNames = {"performance_id", "round_number"}
        ),
        indexes = {
                @Index(name = "idx_round_performance", columnList = "performance_id"),
                @Index(name = "idx_round_start_time", columnList = "startDateTime"),
                @Index(name = "idx_round_entry_time", columnList = "entryDateTime")
        }
)
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

    @Column(nullable = false)
    private LocalDateTime endDateTime;
    private LocalDateTime entryDateTime;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @OneToMany(mappedBy = "performanceRound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public PerformanceRound() {
    }

    public PerformanceRound(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime entryDateTime, int roundNumber) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("start/end must not be null");
        }

        if(!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("endDateTime must be after startDateTime");
        }

        if(entryDateTime != null && entryDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("entryDateTime must not be after startDateTime");
        }

        if(roundNumber <= 0) {
            throw new IllegalArgumentException("roundNumber must be positive");
        }

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
        return Collections.unmodifiableList(seats);
    }

    void setPerformance(Performance performance) {
        this.performance = performance;
    }

    public void addSeat(String seatNo, int price) {
        Seat seat = new Seat(seatNo, price, this);
        this.seats.add(seat);
    }

    public void removeSeat(Seat seat) {
        if(seat == null) return;
        this.seats.remove(seat);
    }
}
