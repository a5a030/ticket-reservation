package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRoundRepository;
import com.byunsum.ticket_reservation.reservation.domain.PreReservation;
import com.byunsum.ticket_reservation.reservation.domain.PreReservationStatus;
import com.byunsum.ticket_reservation.reservation.dto.PreReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.PreReservationResponse;
import com.byunsum.ticket_reservation.reservation.repository.PreReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreReservationService {
    private final MemberRepository memberRepository;
    private final PreReservationRepository preReservationRepository;
    private final PerformanceRoundRepository performanceRoundRepository;

    public PreReservationService(MemberRepository memberRepository, PreReservationRepository preReservationRepository, PerformanceRoundRepository performanceRoundRepository) {
        this.memberRepository = memberRepository;
        this.preReservationRepository = preReservationRepository;
        this.performanceRoundRepository = performanceRoundRepository;
    }

    @Transactional
    public PreReservationResponse apply(Long memberId, PreReservationRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PerformanceRound round = performanceRoundRepository.findById(request.roundId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROUND_NOT_FOUND));

        if(preReservationRepository.existsByMember(member)) {
            throw new CustomException(ErrorCode.DUPLICATE_PRE_RESERVATION);
        }

        PreReservation preReservation = new PreReservation(member, round, PreReservationStatus.WAITING);
        preReservationRepository.save(preReservation);

        return new PreReservationResponse(
                preReservation.getId(),
                round.getId(),
                preReservation.getStatus().name(),
                preReservation.getAppliedAt()
        );
    }
}
