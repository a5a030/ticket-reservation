package com.byunsum.ticket_reservation.performance.service;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import org.springframework.stereotype.Service;

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
}
