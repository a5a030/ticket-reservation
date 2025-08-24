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
                request.getStartDate(),
                request.getEndDate(),
                request.getTime(),
                request.getGenre(),
                request.getPosterUrl(),
                request.getPreReservationOpenDate(),
                request.getGeneralOpenDate(),
                request.getMaxTicketsPerPerson()
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
                        performance.getPosterUrl(),
                        performance.getPreReservationOpenDateTime(),
                        performance.getGeneralReservationOpenDateTime(),
                        performance.getMaxTicketsPerPerson()
                ))
                .collect(Collectors.toList());
    }

    public PerformanceResponse getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

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
                performance.getMaxTicketsPerPerson()
        );
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

        return performances.map(p -> new PerformanceResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getDescription(),
                        p.getVenue(),
                        p.getStartDate(),
                        p.getEndDate(),
                        p.getTime(),
                        p.getGenre(),
                        p.getPosterUrl(),
                        p.getPreReservationOpenDateTime(),
                        p.getGeneralReservationOpenDateTime(),
                        p.getMaxTicketsPerPerson()
                ));
    }
}
