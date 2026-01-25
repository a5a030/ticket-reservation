package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.notification.domain.NotificationType;
import com.byunsum.ticket_reservation.notification.service.NotificationService;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRoundRepository;
import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservation;
import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservationStatus;
import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservationType;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationAdminResponse;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationMyResponse;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationResponse;
import com.byunsum.ticket_reservation.reservation.repository.PreReservationRepository;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PreReservationService {
    private final MemberRepository memberRepository;
    private final PreReservationRepository preReservationRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final NotificationService notificationService;

    public PreReservationService(MemberRepository memberRepository, PreReservationRepository preReservationRepository, PerformanceRepository performanceRepository, SeatRepository seatRepository, NotificationService notificationService) {
        this.memberRepository = memberRepository;
        this.preReservationRepository = preReservationRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PreReservationResponse apply(Long memberId, PreReservationRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));


        Performance performance = performanceRepository.findById(request.performanceId())
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        PreReservationType policyType = performance.getPreReservationType();
        if(request.type() != policyType) {
            throw new CustomException(ErrorCode.INVALID_SALE_POLICY);
        }

        if(preReservationRepository.existsByMemberAndPerformance(member, performance)) {
            throw new CustomException(ErrorCode.DUPLICATE_PRE_RESERVATION);
        }

        PreReservation preReservation = new PreReservation(member, performance, policyType);
        preReservationRepository.save(preReservation);

        return new PreReservationResponse(
                preReservation.getId(),
                performance.getId(),
                performance.getTitle(),
                preReservation.getType().name(),
                preReservation.getStatus().name(),
                preReservation.getAppliedAt()
        );
    }

    @Transactional
    public void drawWinners(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        PreReservationType policyType = performance.getPreReservationType();

        long totalSeats = seatRepository.countByPerformance(performance);

        if(totalSeats <= 0) {
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }

        List<PreReservation> applicants = preReservationRepository.findByPerformanceAndTypeAndStatus(performance, policyType, PreReservationStatus.WAITING);

        if(applicants.isEmpty()) {
            throw new CustomException(ErrorCode.NO_PRE_RESERVATION_APPLICANTS);
        }

        int winnerCount = (int) Math.min(applicants.size(), Math.floor(totalSeats * 0.9));

        Collections.shuffle(applicants);

        LocalDateTime drawnAt = LocalDateTime.now();
        LocalDateTime expiresAt = drawnAt.toLocalDate().atTime(23,59,59);

        for(int i=0; i<applicants.size(); i++) {
            PreReservation pre =  applicants.get(i);

            boolean isWinner = i < winnerCount;

            if(isWinner) {
                if(policyType == PreReservationType.SEAT_ASSIGNMENT) {
                    pre.markWinner(drawnAt, expiresAt);
                } else {
                    pre.markWinner(drawnAt); //PRE_SALE
                }

                notificationService.createNotification(
                        buildWinnerMessage(performance, policyType, expiresAt),
                        pre.getMember(),
                        NotificationType.PRE_RESERVATION
                );
            } else {
                pre.markLoser(drawnAt);

                notificationService.createNotification(
                        "[미당첨] " + performance.getTitle() + " 공연에 당첨되지 못했습니다.",
                        pre.getMember(),
                        NotificationType.PRE_RESERVATION
                );
            }
        }

        preReservationRepository.saveAll(applicants);
    }

    @Transactional(readOnly = true)
    public List<PreReservationMyResponse> getMyPreReservations(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<PreReservation> list = preReservationRepository.findByMember(member);

        return list.stream()
                .map(p -> new PreReservationMyResponse(
                        p.getPerformance().getId(),
                        p.getPerformance().getTitle(),
                        p.getType().name(),
                        p.getStatus().name(),
                        p.getAppliedAt(),
                        p.getExpiresAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PreReservationAdminResponse> getPreReservationsByPerformance(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        List<PreReservation> preReservations = preReservationRepository.findByPerformance(performance);

        return preReservations.stream()
                .map(pre -> new PreReservationAdminResponse(
                        pre.getId(),
                        pre.getMember().getId(),
                        pre.getMember().getEmail(),
                        performance.getId(),
                        pre.getType().name(),
                        pre.getStatus().name(),
                        pre.getAppliedAt()
                ))
                .toList();
    }

    private String buildWinnerMessage(Performance performance, PreReservationType policyType, LocalDateTime expiresAt) {
        if(policyType == PreReservationType.SEAT_ASSIGNMENT) {
            return "[당첨] " + performance.getTitle()
                    + " 좌석 추첨에 당첨되었습니다. 결제 기한: "
                    + expiresAt.toLocalDate() + " 23:59:59";
        }

        return "[당첨] " + performance.getTitle() + " 선예매 대상자로 선정되었습니다. 예매일정을 확인하세요.";
    }
}
