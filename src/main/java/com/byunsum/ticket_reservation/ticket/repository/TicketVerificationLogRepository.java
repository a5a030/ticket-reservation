package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
import com.byunsum.ticket_reservation.ticket.domain.TicketVerifyResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketVerificationLogRepository extends JpaRepository<TicketVerificationLog, Long> {
    Page<TicketVerificationLog> findByVerifiedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<TicketVerificationLog> findByVerifiedAtBetweenAndResult(LocalDateTime start, LocalDateTime end, TicketVerifyResult result, Pageable pageable);

    @Query("SELECT v.result, COUNT(v) FROM TicketVerificationLog  v " +
            "WHERE v.verifiedAt BETWEEN :start AND :end GROUP BY v.result")
    List<Object[]> countByResultBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('HOUR', v.verifiedAt), COUNT(v) FROM TicketVerificationLog v " +
            "WHERE v.verifiedAt BETWEEN :start AND :end GROUP BY FUNCTION('HOUR', v.verifiedAt)")
    List<Object[]> countByHourBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    long countByResultAndVerifiedAtBetween(TicketVerifyResult result, LocalDateTime start, LocalDateTime end);

    long countByVerifiedAtBetween(LocalDateTime start, LocalDateTime end);
}
