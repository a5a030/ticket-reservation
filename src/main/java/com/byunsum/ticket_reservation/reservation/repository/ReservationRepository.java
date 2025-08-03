package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCode(String reservationCode);

    Long id(Long id);
}
