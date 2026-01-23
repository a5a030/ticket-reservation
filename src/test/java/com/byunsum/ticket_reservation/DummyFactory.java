package com.byunsum.ticket_reservation;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.pre.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.reservation.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DummyFactory {
    public static ReservationSeat dummyReservationSeat() {
        Performance performance = new Performance();
        performance.setTitle("테스트 공연");
        performance.setStartDate(LocalDate.now());
        performance.setEndDate(LocalDate.now().plusDays(1));
        performance.setTime(LocalTime.of(19, 0));

        Seat seat = new Seat();
        seat.setSeatNo("A1");
        seat.setPrice(10000);

        Reservation reservation = new Reservation();
        reservation.setPerformance(performance);

        return new ReservationSeat(reservation, seat);
    }

    public static ReservationSeat dummyReservationSeatPersisted(PerformanceRepository performanceRepository,
                                                       MemberRepository memberRepository,
                                                       ReservationRepository reservationRepository,
                                                       SeatRepository seatRepository,
                                                       ReservationSeatRepository reservationSeatRepository) {
        // 1. 공연 생성
        Performance performance = new Performance();
        performance.setTitle("더미 공연");
        performance.setStartDate(LocalDate.now());
        performance.setEndDate(LocalDate.now().plusDays(1));
        performance.setTime(LocalTime.of(19, 0));

        // 2. 라운드 생성
        PerformanceRound round = new PerformanceRound(performance,
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(19, 0)),
                "1회차", 100);
        performance.addRound(round);

        // 3. 좌석 생성
        Seat seat = new Seat("A1", 10000, true, round);
        seatRepository.save(seat);

        // 4. 멤버 생성
        Member member = new Member("dummyUser", "pw", "테스터", "test@test.com", "ROLE_USER");
        memberRepository.save(member);

        // 5. 예약 생성
        Reservation reservation = new Reservation(performance, member);
        reservationRepository.save(reservation);

        // 6. 예약좌석 생성 (연관관계 설정 포함)
        ReservationSeat reservationSeat = new ReservationSeat(reservation, seat);
        reservationSeatRepository.save(reservationSeat);

        return reservationSeat;
    }
}
