package com.byunsum.ticket_reservation.admin.service;

import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import com.byunsum.ticket_reservation.review.service.KeywordService;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.springframework.stereotype.Service;

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


}
