package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.payment.service.PaymentService;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.*;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.ReservationResponse;
import com.byunsum.ticket_reservation.reservation.repository.PreReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final StringRedisTemplate redisTemplate;
    private final PaymentService paymentService;
    private final PreReservationRepository preReservationRepository;

    public ReservationService(ReservationRepository reservationRepository, PerformanceRepository performanceRepository, SeatRepository seatRepository, StringRedisTemplate redisTemplate, PaymentService paymentService, PreReservationRepository preReservationRepository) {
        this.reservationRepository = reservationRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.redisTemplate = redisTemplate;
        this.paymentService = paymentService;
        this.preReservationRepository = preReservationRepository;
    }

    private String getKey(Long seatId) {
        return "seat:selected:" + seatId;
    }

    private Reservation saveReservation(Performance performance, List<Seat> seats, Member member) {
        Reservation reservation = new Reservation();
        reservation.setPerformance(performance);
        reservation.setMember(member);

        for(Seat seat : seats) {
            if(seat.isReserved()) {
                throw new  CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
            }

            reservation.addSeat(seat);
        }

        return reservationRepository.save(reservation);
    }

    private ReservationResponse toResponse(Reservation reservation) {
        Long ttl = 0L;

        if(!reservation.getSeats().isEmpty()) {
            String redisKey = "seat:reconfirm:" + reservation.getSeats().get(0).getId();
            Long expire = redisTemplate.getExpire(redisKey);
            ttl = (expire != null && expire > 0) ? expire : 0L;
        }

        return new ReservationResponse(reservation, ttl);
    }

    public ReservationResponse createReservation(ReservationRequest request, Member member) {
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        validateReservationPeriod(performance, member);

        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        if(seats.size() != request.getSeatIds().size()) {
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }

        Reservation reservation = saveReservation(performance, seats, member);

        for(Seat seat : seats) {
            String timeoutKey = "reservation:timeout:" + seat.getId();
            redisTemplate.opsForValue().set(timeoutKey, "PENDING", Duration.ofMinutes(10));
        }

        return toResponse(reservation);
    }

    public ReservationResponse getReservationByCode(String code) {
        Reservation reservation = reservationRepository.findByReservationCode(code)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse confirmReservation(Long performanceId, List<Long> seatIds, Member member) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        validateReservationPeriod(performance, member);

        List<Seat> seats = seatRepository.findAllById(seatIds);
        if(seats.size() != seatIds.size()) {
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }

        for(Seat seat : seats) {
            String key = getKey(seat.getId());
            String selectedBy = redisTemplate.opsForValue().get(key);

            if(selectedBy == null || !selectedBy.equals(member.getId().toString())) {
                throw new CustomException(ErrorCode.SEAT_ALREADY_SELECTED);
            }

            if(seat.isReserved()) {
                throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
            }
        }

        Reservation reservation = saveReservation(performance, seats, member);

        for(Seat seat : seats) {
            redisTemplate.delete(getKey(seat.getId()));
        }

        return toResponse(reservation);
    }

    @Transactional
    public void cancelReservation(String reservationCode, Long memberId) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(reservation.isCancelled()) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED);
        }

        if(!reservation.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_CANCEL);
        }

        reservation.cancel();
        paymentService.cancelByReservation(reservation);

        for(Seat seat : reservation.getSeats()) {
            // Redis에 취소된 좌석 잠금 처리
            String reconfirmKey = "seat:reconfirm:" + seat.getId();
            redisTemplate.opsForValue().set(reconfirmKey, "LOCKED", Duration.ofMinutes(10));

            // 좌석 선택 키도 일정 시간 후 사용 가능하게 등록
            String seatKey = getKey(seat.getId());
            long randomSeconds = ThreadLocalRandom.current().nextLong(300, 601);
            redisTemplate.opsForValue().set(seatKey, "available", Duration.ofSeconds(randomSeconds));
        }
    }

    @Transactional
    public ReservationResponse reconfirmReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!reservation.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_RECONFIRM);
        }

        if(reservation.getStatus() != ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.INVALID_RECONFIRM_STATUS);
        }

        if(reservation.isReconfirmed()) {
            throw new CustomException(ErrorCode.ALREADY_RECONFIRMED);
        }

        if(reservation.getCancelledAt() == null) {
            throw new CustomException(ErrorCode.RECONFIRM_NOT_ALLOWED);
        }

        if(reservation.getCancelledAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.RECONFIRM_EXPIRED);
        }

        for(Seat seat : reservation.getSeats()) {
            String redisKey = "seat:reconfirm:" + seat.getId();
            String lockValue = redisTemplate.opsForValue().get(redisKey);

            if(lockValue == null || !"LOCKED".equals(lockValue)) {
                throw new CustomException(ErrorCode.SEAT_ALREADY_RELEASED);
            }
        }

        reservation.reconfirm();
        paymentService.restorePayment(reservation.getId());

        return toResponse(reservation);
    }

    public List<ReservationResponse> getReservationsByMember(Long memberId, ReservationSortOption sort) {
        List<Reservation> reservations;

        switch (sort) {
            case IMMINENT:
                reservations = reservationRepository.findByMemberIdOrderByPerformanceImminent(memberId);
                break;
            case OLDEST:
                reservations = reservationRepository.findByMemberIdOrderByCreatedAtAsc(memberId);
                break;
            case RECENT:
            default:
                reservations = reservationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
                break;
        }

        return reservations.stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateReservationPeriod(Performance performance, Member member) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime preStart = performance.getPreReservationOpenDateTime();
        LocalDateTime preEnd = preStart.toLocalDate().atTime(23,59,59);

        if(now.isAfter(preStart) && now.isBefore(preEnd)) {
            PreReservation pre = preReservationRepository.findByMemberAndPerformance(member, performance)
                    .orElseThrow(() -> new CustomException(ErrorCode.PRE_RESERVATION_REQUIRED));

            if(pre.getStatus() != PreReservationStatus.WINNER) {
                throw new CustomException(ErrorCode.NOT_PRE_RESERVATION_WINNER);
            }
        } else if(now.isBefore(performance.getGeneralReservationOpenDateTime())) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_OPEN);
        } else {
            int maxTicketsPerPerson = performance.getMaxTicketsPerPerson();

            if(maxTicketsPerPerson > 0) {
                int reservedCount = reservationRepository.countByMemberAndPerformance(member, performance);

                if(reservedCount >= maxTicketsPerPerson) {
                    throw new CustomException(ErrorCode.EXCEED_MAX_TICKETS);
                }
            }
        }
    }
}
