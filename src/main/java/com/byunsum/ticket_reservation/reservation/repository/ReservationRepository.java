package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationSeatStatus;
import org.springframework.data.domain.Pageable;
import com.byunsum.ticket_reservation.reservation.repository.projection.MemberRoundCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCode(String reservationCode);

    @Query("""
        select r
        from Reservation r
        join r.reservationSeats rs
        where rs.seat.id = :seatId
          and rs.status in :activeStatuses
    """)
    List<Reservation> findActiveReservationsBySeatId(@Param("seatId") Long seatId, @Param("activeStatuses") List<ReservationSeatStatus> activeStatuses);

    List<Reservation> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("""
    SELECT r
    FROM Reservation r
    WHERE r.member.id = :memberId
    ORDER BY (
        SELECT min(pr.startDateTime)
        FROM ReservationSeat rs
        JOIN rs.seat s
        JOIN s.performanceRound pr
        WHERE rs.reservation = r
        ) asc
    """)
    List<Reservation> findByMemberIdOrderByRoundStartDateAsc(@Param("memberId") Long memberId);

    @Query("""
    select r.performance.id
    from Reservation r
    group by r.performance.id
    order by count(r) desc
    """)
    List<Long> findPopularPerformanceIds(Pageable pageable);

    int countByMemberAndPerformance(Member member, Performance performance);

    List<Reservation> findByMemberIdOrderByCreatedAtAsc(Long memberId);

    @Query("""
    SELECT r.member.id as memberId, COUNT(DISTINCT pr.id) as roundCount
    FROM Reservation r
    JOIN r.reservationSeats rs
    JOIN rs.seat s
    JOIN s.performanceRound pr
    WHERE r.performance.id = :performanceId
    GROUP BY r.member.id
    HAVING COUNT(DISTINCT pr.id) > 1
""")
    List<MemberRoundCount> findMembersWithMultipleRounds(@Param("performanceId") Long performanceId);
}

