package com.byunsum.ticket_reservation.batch.job.banktransfer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BankTransferExpirationScheduler {
    private final JobLauncher jobLauncher;
    private final Job bankTransferExpirationJob;

    public BankTransferExpirationScheduler(JobLauncher jobLauncher, Job bankTransferExpirationJob) {
        this.jobLauncher = jobLauncher;
        this.bankTransferExpirationJob = bankTransferExpirationJob;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(bankTransferExpirationJob, jobParameters);

            System.out.println("[BankTransferExpirationScheduler] Job executed at " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("[BankTransferExpirationScheduler] Job failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
