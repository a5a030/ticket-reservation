package com.byunsum.ticket_reservation.batch.processor;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ReservationProcessor implements ItemProcessor<ReservationRequest, Reservation> {
    private final MemberRepository memberRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    public ReservationProcessor(MemberRepository memberRepository, PerformanceRepository performanceRepository, SeatRepository seatRepository) {
        this.memberRepository = memberRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public Reservation process(ReservationRequest request){
        var performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        var member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Reservation reservation = new Reservation();
        reservation.setPerformance(performance);
        reservation.setMember(member);

        request.getSeatIds().forEach(seatId -> {
            var seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

            reservation.addSeat(seat);
        });

        return reservation;
    }
}
