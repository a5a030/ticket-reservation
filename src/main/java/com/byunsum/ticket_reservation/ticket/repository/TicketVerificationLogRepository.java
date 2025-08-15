package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketVerificationLogRepository extends JpaRepository<TicketVerificationLog, Long> {
}
