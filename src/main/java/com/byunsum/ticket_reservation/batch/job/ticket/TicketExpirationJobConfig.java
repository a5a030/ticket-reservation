package com.byunsum.ticket_reservation.batch.job.ticket;

import com.byunsum.ticket_reservation.batch.tasklet.ExpireTicketTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TicketExpirationJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ExpireTicketTasklet expireTicketTasklet;

    public TicketExpirationJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, ExpireTicketTasklet expireTicketTasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.expireTicketTasklet = expireTicketTasklet;
    }

    @Bean
    public Job ticketExpirationJob() {
        return new JobBuilder("ticketExpirationJob", jobRepository)
                .start(expireTicketStep())
                .build();
    }

    @Bean
    public Step expireTicketStep() {
        return new StepBuilder("expireTicketStep", jobRepository)
                .tasklet(expireTicketTasklet, transactionManager)
                .build();
    }
}
