package com.byunsum.ticket_reservation.ticket;

import com.byunsum.ticket_reservation.DummyFactory;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.reservation.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
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

import java.time.Duration;
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
        ReservationSeat seat = DummyFactory.dummyReservationSeatPersisted(performanceRepository, memberRepository, reservationRepository, seatRepository, reservationSeatRepository);

        Ticket ticket = Ticket.create(seat, "qr.png", Duration.ofHours(1));
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
