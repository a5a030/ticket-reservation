package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
