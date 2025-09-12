package com.byunsum.ticket_reservation.performance.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.dto.PerformanceResponse;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceService {
    private final PerformanceRepository performanceRepository;

    public PerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    public PerformanceResponse createPerformance(PerformanceRequest request) {
        Performance performance = new Performance(
                request.getTitle(),
                request.getDescription(),
                request.getVenue(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTime(),
                request.getGenre(),
                request.getPosterUrl(),
                request.getPreReservationOpenDate(),
                request.getGeneralOpenDate(),
                request.getMaxTicketsPerPerson(),
                request.getType(),
                request.getEntryStartTime()
        );

        Performance saved = performanceRepository.save(performance);

        return toResponse(saved);
    }

    public PerformanceResponse updatePerformance(Long id, PerformanceRequest request) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        performance.setTitle(request.getTitle());
        performance.setDescription(request.getDescription());
        performance.setVenue(request.getVenue());
        performance.setStartDate(request.getStartDate());
        performance.setEndDate(request.getEndDate());
        performance.setTime(request.getTime());
        performance.setGenre(request.getGenre());
        performance.setPosterUrl(request.getPosterUrl());
        performance.setPreReservationOpenDateTime(request.getPreReservationOpenDate());
        performance.setGeneralReservationOpenDateTime(request.getGeneralOpenDate());
        performance.setMaxTicketsPerPerson(request.getMaxTicketsPerPerson());
        performance.setType(request.getType());
        performance.setEntryStartTime(request.getEntryStartTime());

        Performance updated = performanceRepository.save(performance);

        return toResponse(updated);
    }

    public List<PerformanceResponse> getAllPerformances() {
        return performanceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PerformanceResponse getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        return toResponse(performance);
    }

    public Page<PerformanceResponse> getPerformanceSorted(String sort, Pageable pageable) {
        Page<Performance> performances;

        if("imminent".equalsIgnoreCase(sort)) {
            performances = performanceRepository.findAllByOrderByStartDateAscTimeAsc(pageable);
        } else if("popular".equalsIgnoreCase(sort)) {
            performances = performanceRepository.findAllOrderByReservationsCountDesc(pageable);
        } else {
            performances = performanceRepository.findAll(pageable);
        }

        return performances.map(this::toResponse);
    }

    private PerformanceResponse toResponse(Performance performance) {
        return new PerformanceResponse(
                performance.getId(),
                performance.getTitle(),
                performance.getDescription(),
                performance.getVenue(),
                performance.getStartDate(),
                performance.getEndDate(),
                performance.getTime(),
                performance.getGenre(),
                performance.getPosterUrl(),
                performance.getPreReservationOpenDateTime(),
                performance.getGeneralReservationOpenDateTime(),
                performance.getMaxTicketsPerPerson(),
                performance.getType(),
                performance.getEntryStartTime()
        );
    }
}
