package com.byunsum.ticket_reservation.admin.service;

import com.byunsum.ticket_reservation.admin.dto.DashboardResponse;
import com.byunsum.ticket_reservation.admin.dto.ReviewStatsResponse;
import com.byunsum.ticket_reservation.admin.dto.SalesStatsResponse;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.payment.service.PaymentService;
import com.byunsum.ticket_reservation.payment.service.PaymentStatisticService;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.domain.SentimentType;
import com.byunsum.ticket_reservation.review.dto.KeywordSummary;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import com.byunsum.ticket_reservation.review.service.KeywordService;
import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {
    private final PerformanceRepository performanceRepository;
    private final ReviewRepository reviewRepository;
    private final KeywordService keywordService;
    private final TicketVerificationLogRepository ticketVerificationLogRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final PaymentStatisticService paymentStatisticService;

    public AdminDashboardService(PerformanceRepository performanceRepository, ReviewRepository reviewRepository, KeywordService keywordService, TicketVerificationLogRepository ticketVerificationLogRepository, PaymentRepository paymentRepository, PaymentService paymentService, PaymentStatisticService paymentStatisticService) {
        this.performanceRepository = performanceRepository;
        this.reviewRepository = reviewRepository;
        this.keywordService = keywordService;
        this.ticketVerificationLogRepository = ticketVerificationLogRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.paymentStatisticService = paymentStatisticService;
    }


    public SalesStatsResponse getSalesStats() {
        BigDecimal totalSales = paymentStatisticService.getTotalPaymentAmount();
        long totalPayments = paymentStatisticService.getTotalPaymentCount();
        BigDecimal average = paymentStatisticService.getAveragePaymentAmount();

        Map<String, BigDecimal> salesByPerformance = paymentStatisticService.getSalesByPerformance()
                .stream()
                .collect(Collectors.toMap(
                        PaymentSalesStatsResponse::groupLabel,
                        PaymentSalesStatsResponse::totalAmount
                ));

        Map<String, BigDecimal> salesByGenre = paymentStatisticService.getSalesByGenre()
                .stream()
                .collect(Collectors.toMap(
                        PaymentSalesStatsResponse::groupLabel,
                        PaymentSalesStatsResponse::totalAmount
                ));

        return new  SalesStatsResponse(
                totalSales,
                totalPayments,
                average,
                salesByPerformance,
                salesByGenre
        );
    }

    public ReviewStatsResponse getReviewStats() {
        List<Review> reviews = reviewRepository.findAll();

        long totalReviews = reviews.size();

        long positive = reviews.stream().filter(r -> r.getSentiment() == SentimentType.POSITIVE).count();
        long negative = reviews.stream().filter(r -> r.getSentiment() == SentimentType.NEGATIVE).count();

        Map<String, Long> sentimentCount = Map.of("POSITIVE", positive, "NEGATIVE", negative);

        List<String> reviewTexts = reviews.stream()
                .map(Review::getContent)
                .toList();

        List<KeywordSummary> keywordSummaries = keywordService.extractTopKeywordsWithCount(reviewTexts, 2, 5);

        Map<String, Long> topKeywords = keywordSummaries.stream()
                .collect(Collectors.toMap(KeywordSummary::getKeyword, KeywordSummary::getCount));

        double averageScore = reviews.stream()
                .mapToDouble(Review::getSentimentScore)
                .average()
                .orElse(0.0);

        List<String> recentSummaries = reviews.stream()
                .filter(r -> r.getSummary() != null)
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(3)
                .map(Review::getSummary)
                .toList();

        return new  ReviewStatsResponse(totalReviews, sentimentCount, topKeywords, averageScore, recentSummaries);
    }

    public VerificationStatsResponse getTicketStats(LocalDateTime start, LocalDateTime end) {
        long total = ticketVerificationLogRepository.countByVerifiedAtBetween(start, end);
        long success =  ticketVerificationLogRepository.countByResultAndVerifiedAtBetween("USED", start, end);
        long fail = total - success;

        double rate = total > 0 ? (double) success / total * 100 : 0.0;
        String successRate = String.format("%.1f%%", rate);

        Map<String, Long> resultCounts = ticketVerificationLogRepository.countByResultBetween(start, end)
                .stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> ((Number) r[1]).longValue()
                ));

        Map<Integer, Long> hourlyCounts = ticketVerificationLogRepository.countByHourBetween(start, end)
                .stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue(),
                        r -> ((Number) r[1]).longValue()
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
