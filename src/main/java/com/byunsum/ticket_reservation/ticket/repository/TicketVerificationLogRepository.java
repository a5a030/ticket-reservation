package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TicketVerificationLogRepository extends JpaRepository<TicketVerificationLog, Long> {
    Page<TicketVerificationLog> findByVerifiedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<TicketVerificationLog> findByVerifiedAtBetweenAndResult(LocalDateTime from, LocalDateTime to, String status, Pageable pageable);
}
