package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class TicketJpaTest {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationSeatRepository reservationSeatRepository;

    @Test
    @DisplayName("티켓 생성 후 JPA 저장 → 상태 ISSUED 확인")
    void createAndPersistTicket() {
        ReservationSeat seat = reservationSeatRepository.save(new ReservationSeat());
        Ticket ticket = Ticket.create(seat, "qr.png", Duration.ofHours(1));
        ticketRepository.save(ticket);

        Ticket found = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(TicketStatus.ISSUED);
    }

    @Test
    @DisplayName("USED 상태 티켓은 다시 검표 불가 (DB 조회 후도 동일)")
    void usedTicketCannotBeReused() {
        ReservationSeat seat = reservationSeatRepository.save(new ReservationSeat());
        Ticket ticket = Ticket.create(seat, "qr.png", Duration.ofHours(1));
        ticket.markUsed();
        ticketRepository.save(ticket);

        Ticket found = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThrows(IllegalStateException.class, found::markUsed);
    }

    @Test
    @DisplayName("ISSUED 상태 티켓은 만료 처리 가능")
    void issuedTicketCanExpire() {
        ReservationSeat seat = reservationSeatRepository.save(new ReservationSeat());
        Ticket ticket = Ticket.create(seat, "qr.png", Duration.ofHours(1));
        ticketRepository.save(ticket);

        Ticket found = ticketRepository.findById(ticket.getId()).orElseThrow();
        found.markExpired();

        assertThat(found.getStatus()).isEqualTo(TicketStatus.EXPIRED);
    }

    @Test
    @DisplayName("USED 상태 티켓은 만료 처리 불가")
    void usedTicketCannotExpire() {
        ReservationSeat seat = reservationSeatRepository.save(new ReservationSeat());
        Ticket ticket = Ticket.create(seat, "qr.png", Duration.ofHours(1));
        ticket.markUsed();
        ticketRepository.save(ticket);

        Ticket found = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThrows(IllegalStateException.class, found::markExpired);
    }
}
