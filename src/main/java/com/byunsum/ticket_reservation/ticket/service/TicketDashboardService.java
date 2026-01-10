package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.ticket.domain.TicketStatus;
import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TicketDashboardService {
    private final TicketVerificationLogRepository logRepository;

    public TicketDashboardService(TicketVerificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public VerificationStatsResponse getStats(LocalDateTime start, LocalDateTime end) {
        long total = logRepository.countByVerifiedAtBetween(start, end);
        long success = logRepository.countByResultAndVerifiedAtBetween(TicketStatus.USED.name(), start, end);

        double rate = total == 0 ? 0 : (double) success / total * 100;
        String successRate = String.format("%.1f%%", rate);

        Map<String , Long> resultCounts = new HashMap<>();
        for(Object[] row : logRepository.countByResultBetween(start, end)) {
            resultCounts.put((String) row[0], ((Number) row[1]).longValue());
        }

        Map<Integer, Long> hourlyCounts = new HashMap<>();
        for(Object[] row : logRepository.countByHourBetween(start, end)) {
            Number hour = (Number) row[0];
            Number count = (Number) row[1];
            hourlyCounts.put(hour.intValue(), count.longValue());
        }

        return new VerificationStatsResponse(successRate, resultCounts, hourlyCounts);
    }
}
