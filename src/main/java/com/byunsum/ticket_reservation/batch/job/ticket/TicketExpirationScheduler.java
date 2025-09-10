package com.byunsum.ticket_reservation.batch.job.ticket;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TicketExpirationScheduler {
    private final JobLauncher jobLauncher;
    private final Job ticketExpirationJob;

    public TicketExpirationScheduler(JobLauncher jobLauncher, Job ticketExpirationJob) {
        this.jobLauncher = jobLauncher;
        this.ticketExpirationJob = ticketExpirationJob;
    }

    // 매일 자정에 만료 티켓 정리 배치 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void run() throws Exception {
        jobLauncher.run(ticketExpirationJob,
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters());
    }
}
