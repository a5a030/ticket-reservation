package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.TicketReissueLog;
import com.byunsum.ticket_reservation.ticket.dto.TicketReissueStatsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketReissueLogRepository extends JpaRepository<TicketReissueLog, Long> {
    List<TicketReissueLog> findByReservationSeatId(Long reservationSeatId);
    List<TicketReissueLog> findByReservationSeatIdOrderByReissueAtDesc(Long reservationSeatId);

    @Query("select new com.byunsum.ticket_reservation.ticket.dto.TicketReissueStatsResponse(l.loginId, COUNT(l)) " +
            "FROM TicketReissueLog l GROUP BY l.loginId ORDER BY COUNT(l) DESC")
    List<TicketReissueStatsResponse> getReissueStatsByMember();

    @Query("select new com.byunsum.ticket_reservation.ticket.dto.TicketReissueStatsResponse(r.performance.title, count(l)) " +
            "from TicketReissueLog  l join l.reservationSeat rs join  rs.reservation r " +
            "group by r.performance.title order by count(l) desc")
    List<TicketReissueStatsResponse> getReissueStatsByPerformance();
}
