package com.byunsum.ticket_reservation.admin.service;

import com.byunsum.ticket_reservation.admin.dto.DashboardResponse;
import com.byunsum.ticket_reservation.admin.dto.ReviewStatsResponse;
import com.byunsum.ticket_reservation.admin.dto.SalesStatsResponse;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.dto.KeywordSummary;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import com.byunsum.ticket_reservation.review.service.KeywordService;
import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {
    private final PerformanceRepository performanceRepository;
    private final ReviewRepository reviewRepository;
    private final KeywordService keywordService;
    private final TicketVerificationLogRepository ticketVerificationLogRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardService(PerformanceRepository performanceRepository, ReviewRepository reviewRepository, KeywordService keywordService, TicketVerificationLogRepository ticketVerificationLogRepository, PaymentRepository paymentRepository) {
        this.performanceRepository = performanceRepository;
        this.reviewRepository = reviewRepository;
        this.keywordService = keywordService;
        this.ticketVerificationLogRepository = ticketVerificationLogRepository;
        this.paymentRepository = paymentRepository;
    }


    public SalesStatsResponse getSalesStats() {
        long totalSales = paymentRepository.getTotalPaymentAmount();
        long totalPayments = paymentRepository.findByStatus(PaymentStatus.PAID).size();

        long average = totalPayments > 0 ? totalSales / totalPayments : 0L;

        return new  SalesStatsResponse(
                BigDecimal.valueOf(totalSales),
                totalPayments,
                BigDecimal.valueOf(average)
        );
    }

    public ReviewStatsResponse getReviewStats() {
        List<Review> reviews = reviewRepository.findAll();

        long totalReviews = reviews.size();

        long positive = reviews.stream().filter(r -> "POSITIVE".equals(r.getSentiment())).count();
        long negative = reviews.stream().filter(r -> "NEGATIVE".equals(r.getSentiment())).count();

        Map<String, Long> sentimentCount = Map.of("POSITIVE", positive, "NEGATIVE", negative);

        List<String> reviewTexts = reviews.stream()
                .map(Review::getContent)
                .toList();

        List<KeywordSummary> keywordSummaries = keywordService.extractTopKeywordsWithCOunt(reviewTexts, 2, 5);

        Map<String, Integer> topKeywords = keywordSummaries.stream()
                .collect(Collectors.toMap(KeywordSummary::getKeyword, KeywordSummary::getCount));

        return new  ReviewStatsResponse(totalReviews, sentimentCount, topKeywords);
    }

    public VerificationStatsResponse getTicketStats(LocalDateTime start, LocalDateTime end) {
        long total = ticketVerificationLogRepository.countByVerifiedAtBetween(start, end);
        long success =  ticketVerificationLogRepository.countByResultAndVerifiedAtBetween("SUCCESS", start, end);
        long fail = ticketVerificationLogRepository.countByResultAndVerifiedAtBetween("FAIL", start, end);

        double successRate = total > 0 ? (double) success / total : 0.0;

        Map<String, Long> resultCounts = ticketVerificationLogRepository.countByResultBetween(start, end)
                .stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));

        Map<Integer, Long> hourlyCounts = ticketVerificationLogRepository.countByHourBetween(start, end)
                .stream()
                .collect(Collectors.toMap(
                        r -> ((Integer) r[0]),
                        r -> (Long) r[1]
                ));

        return new  VerificationStatsResponse(successRate, resultCounts, hourlyCounts);
    }

    public DashboardResponse getDashboard(LocalDateTime start, LocalDateTime end) {
        SalesStatsResponse sales = getSalesStats();
        ReviewStatsResponse reviews = getReviewStats();
        VerificationStatsResponse tickets = getTicketStats(start, end);

        return new DashboardResponse(sales, reviews, tickets);
    }
}
