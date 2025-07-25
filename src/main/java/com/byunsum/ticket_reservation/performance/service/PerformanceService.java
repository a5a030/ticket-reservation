package com.byunsum.ticket_reservation.performance.service;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.dto.PerformanceResponse;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceService {
    private final PerformanceRepository performanceRepository;

    public PerformanceService(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    public void createPerformance(PerformanceRequest request) {
        Performance performance = new Performance(
                request.getTitle(),
                request.getDescription(),
                request.getVenue(),
                request.getStarDate(),
                request.getEndDate(),
                request.getTime(),
                request.getGenre(),
                request.getPosterUrl()
        );

        performanceRepository.save(performance);
    }

    public List<PerformanceResponse> getAllPerformances() {
        return performanceRepository.findAll()
                .stream()
                .map(performance -> new PerformanceResponse(
                        performance.getId(),
                        performance.getTitle(),
                        performance.getDescription(),
                        performance.getVenue(),
                        performance.getStartDate(),
                        performance.getEndDate(),
                        performance.getTime(),
                        performance.getGenre(),
                        performance.getPosterUrl()
                ))
                .collect(Collectors.toList());
    }

    public PerformanceResponse getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        return new PerformanceResponse(
                performance.getId(),
                performance.getTitle(),
                performance.getDescription(),
                performance.getVenue(),
                performance.getStartDate(),
                performance.getEndDate(),
                performance.getTime(),
                performance.getGenre(),
                performance.getPosterUrl()
        );
    }
}
