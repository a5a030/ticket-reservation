package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRoundRepository;
import com.byunsum.ticket_reservation.reservation.domain.PreReservation;
import com.byunsum.ticket_reservation.reservation.domain.PreReservationStatus;
import com.byunsum.ticket_reservation.reservation.dto.PreReservationMyResponse;
import com.byunsum.ticket_reservation.reservation.dto.PreReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.PreReservationResponse;
import com.byunsum.ticket_reservation.reservation.repository.PreReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class PreReservationService {
    private final MemberRepository memberRepository;
    private final PreReservationRepository preReservationRepository;
    private final PerformanceRoundRepository performanceRoundRepository;
    private final PerformanceRepository performanceRepository;

    public PreReservationService(MemberRepository memberRepository, PreReservationRepository preReservationRepository, PerformanceRoundRepository performanceRoundRepository, PerformanceRepository performanceRepository) {
        this.memberRepository = memberRepository;
        this.preReservationRepository = preReservationRepository;
        this.performanceRoundRepository = performanceRoundRepository;
        this.performanceRepository = performanceRepository;
    }

    @Transactional
    public PreReservationResponse apply(Long memberId, PreReservationRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Performance performance = performanceRepository.findById(request.performanceId())
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        if(preReservationRepository.existsByMemberAndPerformance(member, performance)) {
            throw new CustomException(ErrorCode.DUPLICATE_PRE_RESERVATION);
        }

        PreReservation preReservation = new PreReservation(member, performance, PreReservationStatus.WAITING);
        preReservationRepository.save(preReservation);

        return new PreReservationResponse(
                preReservation.getId(),
                performance.getId(),
                preReservation.getStatus().name(),
                preReservation.getAppliedAt()
        );
    }

    @Transactional
    public void drawWinners(Long performanceId, int winnerCount) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        List<PreReservation> applicants = preReservationRepository.findByPerformance(performance);

        if(applicants.isEmpty()) {
            throw new CustomException(ErrorCode.NO_PRE_RESERVATION_APPLICANTS);
        }

        Collections.shuffle(applicants);

        for(int i=0; i<applicants.size(); i++) {
            PreReservation pre =  applicants.get(i);

            if(i<winnerCount) {
                pre.setStatus(PreReservationStatus.WINNER);
            } else {
                pre.setStatus(PreReservationStatus.LOSER);
            }
        }

        preReservationRepository.saveAll(applicants);
    }

    @Transactional
    public List<PreReservationMyResponse> getMyPreReservations(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<PreReservation> list = preReservationRepository.findByMember(member);

        return list.stream()
                .map(p -> new PreReservationMyResponse(
                        p.getPerformance().getId(),
                        p.getPerformance().getTitle(),
                        p.getStatus().name(),
                        p.getAppliedAt()
                ))
                .toList();
    }
}
