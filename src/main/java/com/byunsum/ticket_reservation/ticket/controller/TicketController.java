package com.byunsum.ticket_reservation.ticket.controller;

import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.dto.TicketVerifyRequest;
import com.byunsum.ticket_reservation.ticket.dto.TicketVerifyResponse;
import com.byunsum.ticket_reservation.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@Tag(name = "티켓 발급 및 조회", description = "티켓 발급, 조회 및 QR 코드 재발급 API")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/{reservationId}")
    @Operation(summary = "티켓 발급", description = "예약 ID를 기반으로 티켓을 발급합니다.")
    public ResponseEntity<Ticket> issueTicket(@PathVariable Long reservationId) {
        Ticket ticket = ticketService.issueTicket(reservationId);

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/{reservationId}")
    @Operation(summary = "티켓 조회", description = "예약 ID를 기반으로 티켓 정보를 조회합니다.")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long reservationId) {
        Ticket ticket = ticketService.getTicketByReservationId(reservationId);

        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{reservationId}/qr")
    @Operation(summary = "QR 코드 재발급", description = "예약 ID 기반으로 QR 코드를 재발급합니다. (공연 시작 3시간 전부터 가능)")
    public ResponseEntity<QrResponse> refreshQR(@PathVariable Long reservationId) {
        Ticket updatedTicket = ticketService.refreshQrCode(reservationId);

        return ResponseEntity.ok(new QrResponse(reservationId, updatedTicket.getQrImageUrl()));
    }

    @PostMapping("/verify")
    @Operation(summary = "QR 티켓 검증", description = "QR 코드(티켓 코드)로 티켓을 검증합니다. 관리자/검표 전용")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketVerifyResponse> verifyTicket(@RequestBody TicketVerifyRequest request) {
        TicketVerifyResponse response = ticketService.verifyTicket(request.ticketCode());

        return ResponseEntity.ok(response);
    }

    public record QrResponse(
            @Schema(description = "예약 ID")
            Long reservationId,

            @Schema(description = "Base64 인코딩된 QR 이미지 URL", example = "data:image/png;base64,iVBORw0...")
            String qrBase64) {}
}