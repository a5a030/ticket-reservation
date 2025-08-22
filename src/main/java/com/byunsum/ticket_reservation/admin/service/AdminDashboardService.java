package com.byunsum.ticket_reservation.admin.service;

import com.byunsum.ticket_reservation.admin.dto.ReviewStatsResponse;
import com.byunsum.ticket_reservation.admin.dto.SalesStatsResponse;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import com.byunsum.ticket_reservation.review.service.KeywordService;
import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AdminDashboardService {
    private final PerformanceRepository performanceRepository;
    private final ReviewRepository reviewRepository;
    private final KeywordService keywordService;
    private final TicketVerificationLogRepository ticketVerificationLogRepository;

    public AdminDashboardService(PerformanceRepository performanceRepository, ReviewRepository reviewRepository, KeywordService keywordService, TicketVerificationLogRepository ticketVerificationLogRepository) {
        this.performanceRepository = performanceRepository;
        this.reviewRepository = reviewRepository;
        this.keywordService = keywordService;
        this.ticketVerificationLogRepository = ticketVerificationLogRepository;
    }


    public SalesStatsResponse getSalesStats() {
        return new  SalesStatsResponse(
                BigDecimal.valueOf(1000000),
                120L,
                BigDecimal.valueOf(8333)
        );
    }

    public ReviewStatsResponse getReviewStats() {
        return new  ReviewStatsResponse(
                52L,
                Map.of("POSITIVE", 40L, "NEGATIVE", 12L),
                Map.of("재밌다", 20L, "배우", 10L)
        );
    }

    public VerificationStatsResponse getTicketStats() {
        return new  VerificationStatsResponse(
                0.92,
                Map.of("SUCCESS", 120L, "FAIL", 15L),
                Map.of(10,12L,11,20L,12,5L)
        );
    }
}
