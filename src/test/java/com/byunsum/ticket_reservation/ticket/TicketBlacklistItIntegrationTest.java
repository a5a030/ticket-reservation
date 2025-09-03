package com.byunsum.ticket_reservation.ticket;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import com.byunsum.ticket_reservation.ticket.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class TicketBlacklistItIntegrationTest {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    ReservationSeatRepository reservationSeatRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("취소된 티켓은 공연 종료일+1dlf TTL로 블랙리스트 등록된다")
    void cancelledTicket_blacklistExpireAtPerformanceEndPlusDay() {
        //given
        Performance performance = new Performance();
        performance.setTitle("테스트 공연");
        performance.setStartDate(LocalDate.now());
        performance.setEndDate(LocalDate.now().plusDays(1));
        performance.setTime(LocalTime.of(23, 0));
        performanceRepository.save(performance);

        PerformanceRound round = new PerformanceRound(performance,
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(19,0)),
                "1회차", 100);
        performance.addRound(round);

        Seat seat = new Seat("A1", 10000, true, round);
        seatRepository.save(seat);

        Member member = new Member("id", "pw", "테스터", "test@test.com", "ROLE_USER");
        memberRepository.save(member);

        Reservation reservation = new Reservation(performance, member);
        reservationRepository.save(reservation);

        ReservationSeat rs = new ReservationSeat(reservation, seat);
        reservationSeatRepository.save(rs);

        Ticket ticket = Ticket.create(rs, "qr.png", java.time.Duration.ofHours(1));
        ticketRepository.save(ticket);

        //when
        ticketService.cancelTicket(ticket.getTicketCode());

        //then
        String key = "blacklist:ticket:" + ticket.getTicketCode();
        Long ttlSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        assertThat(ttlSeconds).isPositive();
        assertThat(ttlSeconds).isGreaterThan(24*60*60 -10);
    }
}
