package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    Optional<ReservationSeat> findBySeatId(Long seatId);
    List<ReservationSeat> findAllBySeatId(Long seatId);

    @Query("select rs from ReservationSeat rs where rs.reservation.id = :reservationId")
    List<ReservationSeat> findAllByReservationId(@Param("reservationId") Long reservationId);
}
