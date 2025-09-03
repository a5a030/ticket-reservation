package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TicketUnitTest {
    private ReservationSeat dummySeat() {
        return new ReservationSeat();
    }

    @Test
    @DisplayName("RESERVED -> PAID 전이는 허용")
    void reservedToPaid() {
        assertTrue(TicketStatus.RESERVED.canTransitionTo(TicketStatus.PAID));
    }

    @Test
    @DisplayName("RESERVED → USED 전이는 불가")
    void reservedToUsedNotAllowed() {
        assertTrue(TicketStatus.RESERVED.canTransitionTo(TicketStatus.USED));
    }

    @Test
    @DisplayName("티켓 생성 시 상태는 ISSUED")
    void createTicketIssued() {
        Ticket ticket = Ticket.create(dummySeat(), "qr.png", Duration.ofHours(1));
        assertEquals(TicketStatus.ISSUED, ticket.getStatus());
    }

    @Test
    @DisplayName("발급된 티켓은 검표 후 USED 상태로 전환")
    void markUsedSuccess() {
        Ticket ticket = Ticket.create(dummySeat(), "qr.png", Duration.ofHours(1));
        ticket.markUsed();
        assertEquals(TicketStatus.USED, ticket.getStatus());
    }

    @Test
    @DisplayName("USED 상태 티켓은 다시 검표 불가")
    void markUsedTwiceFails() {
        Ticket ticket = Ticket.create(dummySeat(), "qr.png", Duration.ofHours(1));
        ticket.markUsed();
        assertThrows(IllegalStateException.class, ticket::markUsed);
    }

    @Test
    @DisplayName("재발급 시 기존 티켓 INVALIDATED → 새 티켓 ISSUED")
    void refreshTicket() {
        Ticket ticket = Ticket.create(dummySeat(), "qr.png", Duration.ofHours(1));
        ticket.refresh("newCode", "newQr.png", Duration.ofHours(2));

        assertEquals(TicketStatus.ISSUED, ticket.getStatus());
        assertEquals("newCode", ticket.getTicketCode());
    }

    @Test
    @DisplayName("만료 처리 시 상태가 EXPIRED로 전환")
    void expireTicket() {
        Ticket ticket = Ticket.create(dummySeat(), "qr.png", Duration.ofSeconds(1));
        ticket.markExpired();

        assertEquals(TicketStatus.EXPIRED, ticket.getStatus());
    }
}
