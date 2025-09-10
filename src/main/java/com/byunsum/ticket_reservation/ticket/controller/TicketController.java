package com.byunsum.ticket_reservation.ticket.controller;

import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.dto.TicketVerifyRequest;
import com.byunsum.ticket_reservation.ticket.dto.TicketVerifyResponse;
import com.byunsum.ticket_reservation.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@Tag(name = "티켓 발급 및 조회", description = "티켓 발급, 조회 및 QR 코드 재발급 API")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/{reservationId}")
    @Operation(summary = "티켓 발급", description = "예약 ID를 기반으로 좌석별 티켓을 발급합니다.")
    public ResponseEntity<List<Ticket>> issueTickets(@PathVariable Long reservationId) {
        List<Ticket> tickets = ticketService.issueTickets(reservationId);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{reservationId}")
    @Operation(summary = "티켓 조회", description = "예약 ID를 기반으로 발급된 모든 티켓 정보를 조회합니다.")
    public ResponseEntity<List<Ticket>> getTickets(@PathVariable Long reservationId) {
        List<Ticket> tickets = ticketService.getTicketsByReservationId(reservationId);

        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{reservationSeatId}/qr")
    @Operation(summary = "QR 코드 재발급", description = "좌석 단위로 QR 코드를 재발급합니다. (공연 시작 3시간 전부터 가능)")
    public ResponseEntity<QrResponse> refreshQR(@PathVariable Long reservationSeatId) {
        Ticket updatedTicket = ticketService.refreshQrCode(reservationSeatId);

        return ResponseEntity.ok(new QrResponse(reservationSeatId, updatedTicket.getQrImageUrl()));
    }

    @PutMapping("/{reservationId}/qr/all")
    @Operation(summary = "QR 코드 재발급 (다좌석 예약)", description = "예약 ID에 포함된 모든 좌석의 QR 코드를 일괄 재발급합니다. (공연 시작 3시간 전부터 가능)")
    public ResponseEntity<List<QrResponse>> refreshQrForReservation(@PathVariable Long reservationId) {
        List<Ticket> updatedTickets = ticketService.refreshQrCodes(reservationId);

        List<QrResponse> responses = updatedTickets.stream()
                .map(ticket -> new QrResponse(
                        ticket.getReservationSeat().getId(),
                        ticket.getQrImageUrl()
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }


    @PostMapping("/verify")
    @Operation(summary = "QR 티켓 검증", description = "QR 코드(티켓 코드)로 티켓을 검증합니다. 관리자/검표 전용")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketVerifyResponse> verifyTicket(@RequestBody TicketVerifyRequest request, HttpServletRequest servletRequest) {
        TicketVerifyResponse response = ticketService.verifyTicket(request.ticketCode(), servletRequest);

        return ResponseEntity.ok(response);
    }

    public record QrResponse(
            @Schema(description = "예약 좌석 ID")
            Long reservationSeatId,

            @Schema(description = "QR 코드 이미지 URL (Base64 인코딩 또는 이미지 경로 URL)",
                    example = "data:image/png;base64,iVBORw0... 또는 https://.../qr.png")
            String qrImageUrl) {}
}