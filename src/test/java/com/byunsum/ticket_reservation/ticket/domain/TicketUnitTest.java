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
        assertFalse(TicketStatus.RESERVED.canTransitionTo(TicketStatus.USED));
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

    @Test
    @DisplayName("PAID -> REFUNDED 전이는 허용")
    void paidToRefundedAllowed() {
        assertTrue(TicketStatus.PAID.canTransitionTo(TicketStatus.REFUNDED));
    }

    @Test
    @DisplayName("ISSUED -> CANCELLED")
    void issuedToCancelledAllowed() {
        assertTrue(TicketStatus.ISSUED.canTransitionTo(TicketStatus.CANCELLED));
    }

    @Test
    @DisplayName("ISSUED -> EXPIRED")
    void issuedToExpiredAllowed() {
        assertTrue(TicketStatus.ISSUED.canTransitionTo(TicketStatus.EXPIRED));
    }

    @Test
    @DisplayName("USED 상태는 추가 전이 불가")
    void usedNoFurtherTransition() {
        assertFalse(TicketStatus.USED.canTransitionTo(TicketStatus.CANCELLED));
        assertFalse(TicketStatus.USED.canTransitionTo(TicketStatus.REFUNDED));
        assertFalse(TicketStatus.USED.canTransitionTo(TicketStatus.EXPIRED));
    }

    @Test
    @DisplayName("CANCELLED 상태는 추가 전이 불가")
    void cancelledNoFurtherTransition() {
        assertFalse(TicketStatus.CANCELLED.canTransitionTo(TicketStatus.ISSUED));
    }

    @Test
    @DisplayName("REFUNDED 상태는 추가 전이 불가")
    void refundedNoFurtherTransition() {
        assertFalse(TicketStatus.REFUNDED.canTransitionTo(TicketStatus.ISSUED));
    }

    @Test
    @DisplayName("INVALIDATED 상태는 추가 전이 불가")
    void invalidatedNoFurtherTransition() {
        assertFalse(TicketStatus.INVALIDATED.canTransitionTo(TicketStatus.ISSUED));
    }

    @Test
    @DisplayName("EXPIRED 상태는 추가 전이 불가")
    void expiredNoFurtherTransition() {
        assertFalse(TicketStatus.EXPIRED.canTransitionTo(TicketStatus.REFUNDED));
    }
}
