package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.TicketReissueLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TicketReissueLogRepository extends CrudRepository<TicketReissueLog, Long> {
    List<TicketReissueLog> findByReservationSeatId(Long reservationSeatId);
    List<TicketReissueLog> findByReservationSeatIdOrderByReissueAtDesc(Long reservationSeatId);
}
