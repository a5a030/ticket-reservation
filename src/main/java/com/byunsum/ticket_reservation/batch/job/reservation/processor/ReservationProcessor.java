package com.byunsum.ticket_reservation.batch.job.reservation.processor;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.dto.BatchReservationRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationProcessor implements ItemProcessor<BatchReservationRequest, Reservation> {
    private final MemberRepository memberRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    public ReservationProcessor(MemberRepository memberRepository, PerformanceRepository performanceRepository, SeatRepository seatRepository) {
        this.memberRepository = memberRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public Reservation process(BatchReservationRequest request){
        var performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        var member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Reservation reservation = new Reservation(performance, member);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime holdExpiredAt = now;

        var seatIds = request.getSeatIds();
        if(seatIds.size() != seatIds.stream().distinct().count()) {
            throw new  CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        var seats = seatRepository.findAllById(seatIds);

        if(seats.size() != seatIds.size()) {
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }

        seats.forEach(seat -> reservation.addSeat(seat, holdExpiredAt));

        reservation.confirmAllSeats(now);

        return reservation;
    }
}
