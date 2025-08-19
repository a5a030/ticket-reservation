package com.byunsum.ticket_reservation.ticket.controller;

import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "관리자 티켓 API", description = "티켓 검증, 목록 조회 등 관리자 기능")
@RestController
@RequestMapping("/admin/tickets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {
    private final TicketVerificationLogRepository ticketVerificationLogRepository;

    public AdminTicketController(TicketVerificationLogRepository ticketVerificationLogRepository) {
        this.ticketVerificationLogRepository = ticketVerificationLogRepository;
    }

    @Operation(summary = "티켓 검증 로그 조회", description = "기간 및 상태별로 티켓 검증 로그를 조회합니다.")
    @GetMapping("/logs")
    public Page<TicketVerificationLog> getVerificationLogs(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                           @RequestParam(required = false) String status,
                                                           Pageable pageable) {
        if(status != null && !status.isBlank()) {
            return ticketVerificationLogRepository.findByVerifiedAtBetweenAndResult(from, to, status, pageable);
        }

        return  ticketVerificationLogRepository.findByVerifiedAtBetween(from, to, pageable);
    }
}
