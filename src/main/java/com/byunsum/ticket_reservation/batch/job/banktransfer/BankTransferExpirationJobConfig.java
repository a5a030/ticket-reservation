package com.byunsum.ticket_reservation.batch.job.banktransfer;

import com.byunsum.ticket_reservation.batch.tasklet.BankTransferExpirationTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BankTransferExpirationJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final BankTransferExpirationTasklet tasklet;

    public BankTransferExpirationJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, BankTransferExpirationTasklet tasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.tasklet = tasklet;
    }

    @Bean
    public Job bankTransferExpirationJob() {
        return new JobBuilder("bankTransferExpirationJob", jobRepository)
                .start(bankTransferExpireStep())
                .build();
    }

    @Bean
    public Step bankTransferExpireStep() {
        return new StepBuilder("expireStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
