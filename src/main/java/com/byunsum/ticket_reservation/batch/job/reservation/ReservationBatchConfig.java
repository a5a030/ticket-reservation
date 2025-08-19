package com.byunsum.ticket_reservation.batch.job.reservation;

import com.byunsum.ticket_reservation.batch.processor.ReservationProcessor;
import com.byunsum.ticket_reservation.batch.reader.FakeReservationReader;
import com.byunsum.ticket_reservation.batch.writer.ReservationWriter;
import com.byunsum.ticket_reservation.member.repository.MemberRepository;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ReservationBatchConfig {
    @Bean
    public Job reservationJob(JobRepository jobRepository, Step reservationStep, Step performanceStep, Step seatStep) {
        return new JobBuilder("reservationJob", jobRepository)
                .start(reservationStep)
                .build();
    }

    @Bean
    public Step reservationStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                MemberRepository memberRepository,
                                PerformanceRepository performanceRepository,
                                SeatRepository seatRepository,
                                ReservationWriter reservationWriter) {
        return new StepBuilder("reservationStep", jobRepository)
                .<ReservationRequest, Reservation>chunk(100, transactionManager)
                .reader(new FakeReservationReader())
                .processor(new ReservationProcessor(memberRepository, performanceRepository, seatRepository))
                .writer(reservationWriter)
                .build();
    }
}
