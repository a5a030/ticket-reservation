package com.byunsum.ticket_reservation.batch.job.reservation;

import com.byunsum.ticket_reservation.batch.job.reservation.processor.ReservationProcessor;
import com.byunsum.ticket_reservation.batch.job.reservation.reader.FakeReservationReader;
import com.byunsum.ticket_reservation.batch.job.reservation.writer.ReservationWriter;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.pre.Reservation;
import com.byunsum.ticket_reservation.reservation.dto.reservation.BatchReservationRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ReservationBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ReservationBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job reservationJob(@Qualifier("reservationStep") Step reservationStep) {
        return new JobBuilder("reservationJob", jobRepository)
                .start(reservationStep)
                .build();
    }

    @Bean
    public Step reservationStep(MemberRepository memberRepository,
                                PerformanceRepository performanceRepository,
                                SeatRepository seatRepository,
                                ReservationWriter reservationWriter) {
        return new StepBuilder("reservationStep", jobRepository)
                .<BatchReservationRequest, Reservation>chunk(100, transactionManager)
                .reader(new FakeReservationReader(seatRepository, 100, 500))
                .processor(new ReservationProcessor(memberRepository, performanceRepository, seatRepository))
                .writer(reservationWriter)
                .build();
    }
}
