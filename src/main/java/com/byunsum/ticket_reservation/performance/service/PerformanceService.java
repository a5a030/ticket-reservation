package com.byunsum.ticket_reservation.performance.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.dto.PerformanceDetailResponse;
import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.dto.PerformanceRoundResponse;
import com.byunsum.ticket_reservation.performance.dto.PerformanceSummaryResponse;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRoundRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final PerformanceRoundRepository performanceRoundRepository;

    public PerformanceService(PerformanceRepository performanceRepository, PerformanceRoundRepository performanceRoundRepository) {
        this.performanceRepository = performanceRepository;
        this.performanceRoundRepository = performanceRoundRepository;
    }

    @Transactional
    public Long create(PerformanceRequest request) {
        validateRequest(request);

        Performance performance = new Performance(
                request.getTitle(),
                request.getDescription(),
                request.getVenue(),
                request.getGenre(),
                request.getPosterUrl(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPreReservationOpenDateTime(),
                request.getGeneralReservationOpenDateTime(),
                request.getMaxTicketsPerPerson(),
                request.getType()
        );

        Performance saved = performanceRepository.save(performance);
        return saved.getId();
    }

    @Transactional
    public PerformanceSummaryResponse update(Long id, PerformanceRequest request) {
        validateRequest(request);

        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        performance.updateFrom(request);

        return toSummaryResponse(performance);
    }

    @Transactional(readOnly = true)
    public List<PerformanceSummaryResponse> getAllPerformances() {
        return performanceRepository.findAll()
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PerformanceSummaryResponse> getAllPerformances(Pageable pageable) {
        return performanceRepository.findAll(pageable)
                .map(this::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public PerformanceDetailResponse getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        List<PerformanceRound> rounds = performanceRoundRepository
                .findByPerformanceIdOrderByStartDateTimeAsc(id);

        List<PerformanceRoundResponse> roundResponses = rounds.stream()
                .map(this::toRoundResponse)
                .collect(Collectors.toList());

        return toDetailResponse(performance, roundResponses);
    }

    @Transactional(readOnly = true)
    public Page<PerformanceSummaryResponse> getPerformanceSorted(String sort, Pageable pageable) {
        String normalized = (sort == null) ? null : sort.trim().toLowerCase();
        if (normalized != null && normalized.isEmpty()) normalized = null;

        if (normalized != null && !List.of("imminent", "popular", "recent").contains(normalized)) {
            throw new CustomException(ErrorCode.INVALID_SORT);
        }

        return performanceRepository.findAll(pageable).map(this::toSummaryResponse);
    }

    private PerformanceSummaryResponse toSummaryResponse(Performance performance) {
        return new PerformanceSummaryResponse(
                performance.getId(),
                performance.getTitle(),
                performance.getDescription(),
                performance.getVenue(),
                performance.getGenre(),
                performance.getPosterUrl(),
                performance.getStartDate(),
                performance.getEndDate(),
                performance.getPreReservationOpenDateTime(),
                performance.getGeneralReservationOpenDateTime(),
                performance.getMaxTicketsPerPerson(),
                performance.getType()
        );
    }

    private PerformanceRoundResponse toRoundResponse(PerformanceRound round) {
        return new PerformanceRoundResponse(
                round.getId(),
                round.getStartDateTime(),
                round.getEndDateTime(),
                round.getEntryDateTime(),
                round.getRoundNumber()
        );
    }

    private PerformanceDetailResponse toDetailResponse(Performance performance, List<PerformanceRoundResponse> rounds) {
        return new PerformanceDetailResponse(
                performance.getId(),
                performance.getTitle(),
                performance.getDescription(),
                performance.getVenue(),
                performance.getGenre(),
                performance.getPosterUrl(),
                performance.getStartDate(),
                performance.getEndDate(),
                performance.getPreReservationOpenDateTime(),
                performance.getGeneralReservationOpenDateTime(),
                performance.getMaxTicketsPerPerson(),
                performance.getType(),
                rounds
        );
    }

    private void validateRequest(PerformanceRequest request) {
        if(request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }

        LocalDateTime pre = request.getPreReservationOpenDateTime();
        LocalDateTime general = request.getGeneralReservationOpenDateTime();

        if(pre != null && general != null && pre.isAfter(general)) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_OPEN_TIME);
        }
    }
}
