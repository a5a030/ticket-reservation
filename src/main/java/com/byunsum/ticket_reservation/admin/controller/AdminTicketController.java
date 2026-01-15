package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.ticket.domain.TicketReissueLog;
import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
import com.byunsum.ticket_reservation.ticket.domain.TicketVerifyResult;
import com.byunsum.ticket_reservation.ticket.repository.TicketReissueLogRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "관리자 티켓 로그 API", description = "티켓 검증 로그 및 재발급 로그 조회")
@RestController
@RequestMapping("/admin/tickets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {
    private final TicketVerificationLogRepository ticketVerificationLogRepository;
    private final TicketReissueLogRepository ticketReissueLogRepository;

    public AdminTicketController(TicketVerificationLogRepository ticketVerificationLogRepository, TicketReissueLogRepository ticketReissueLogRepository) {
        this.ticketVerificationLogRepository = ticketVerificationLogRepository;
        this.ticketReissueLogRepository = ticketReissueLogRepository;
    }

    @Operation(summary = "티켓 검증 로그 조회", description = "기간 및 상태별로 티켓 검증 로그를 조회합니다.")
    @GetMapping("/logs")
    public Page<TicketVerificationLog> getVerificationLogs(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                           @RequestParam(required = false) TicketVerifyResult result,
                                                           @ParameterObject Pageable pageable) {
        if(result != null) {
            return ticketVerificationLogRepository.findByVerifiedAtBetweenAndResult(from, to, result, pageable);
        }

        return  ticketVerificationLogRepository.findByVerifiedAtBetween(from, to, pageable);
    }

    @Operation(summary = "티켓 재발급 로그 조회", description = "특정 좌석의 티켓 재발급 로그를 조회합니다.")
    @GetMapping("/{reservationSeatId}/reissue-logs")
    public List<TicketReissueLog> getTicketReissueLogs(@PathVariable Long reservationSeatId) {
        return ticketReissueLogRepository.findByReservationSeatId(reservationSeatId);
    }
}
