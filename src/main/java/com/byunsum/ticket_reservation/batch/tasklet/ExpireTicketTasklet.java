package com.byunsum.ticket_reservation.batch.tasklet;

import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.domain.TicketStatus;
import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ExpireTicketTasklet implements Tasklet {
    private final TicketRepository ticketRepository;
    private final TicketVerificationLogRepository ticketVerificationLogRepository;
    private final StringRedisTemplate stringRedisTemplate;

    public ExpireTicketTasklet(TicketRepository ticketRepository, TicketVerificationLogRepository ticketVerificationLogRepository, StringRedisTemplate stringRedisTemplate) {
        this.ticketRepository = ticketRepository;
        this.ticketVerificationLogRepository = ticketVerificationLogRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        List<Ticket> expired = ticketRepository.findExpiredTickets(now);

        if(expired.isEmpty()) return RepeatStatus.FINISHED;

        for(Ticket ticket : expired){
            ticket.setStatus(TicketStatus.EXPIRED);

            String blacklistKey = "blacklist:ticket:" + ticket.getTicketCode();
            stringRedisTemplate.opsForValue().set(blacklistKey, "true");

            LocalDateTime perfEnd = ticket.getReservationSeat()
                    .getReservation().getPerformance().getEndDateTime();

            if(perfEnd != null) {
                long ttlSeconds = Duration.between(now, perfEnd.plusDays(1)).getSeconds();
                stringRedisTemplate.expire(blacklistKey, ttlSeconds, TimeUnit.SECONDS);
            }
        }

        ticketRepository.saveAll(expired);

        List<TicketVerificationLog> logs = expired.stream()
                .map(ticket -> new TicketVerificationLog(
                        ticket.getTicketCode(),
                        "BATCH",
                        "system",
                        TicketStatus.EXPIRED.name(),
                        now
                ))
                .toList();

        ticketVerificationLogRepository.saveAll(logs);

        System.out.println("만료된 티켓 수: " + expired.size());

        return RepeatStatus.FINISHED;
    }
}
