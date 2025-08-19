package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TicketDashboardService {
    private final TicketVerificationLogRepository logRepository;

    public TicketDashboardService(TicketVerificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public VerificationStatsResponse getStats(LocalDateTime start, LocalDateTime end) {
        long total = logRepository.count();
        long success = logRepository.countByResultAndVerifiedAtBetween("USED", start, end);

        double successRate = total == 0 ? 0 : (double) success / total * 100;

        Map<String , Long> resultCounts = new HashMap<>();
        for(Object[] row : logRepository.countByResultBetween(start, end)) {
            resultCounts.put((String) row[0], (Long) row[1]);
        }

        Map<Integer, Long> hourlyCounts = new HashMap<>();
        for(Object[] row : logRepository.countByHourBetween(start, end)) {
            hourlyCounts.put((Integer) row[0], (Long) row[1]);
        }

        return new VerificationStatsResponse(successRate, resultCounts, hourlyCounts);
    }
}
