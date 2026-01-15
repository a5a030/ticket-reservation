package com.byunsum.ticket_reservation.batch.tasklet;

import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.domain.TicketStatus;
import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
import com.byunsum.ticket_reservation.ticket.domain.TicketVerifyResult;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ExpireTicketTasklet implements Tasklet {
    private static final Logger log = LoggerFactory.getLogger(ExpireTicketTasklet.class);

    private final TicketRepository ticketRepository;
    private final TicketVerificationLogRepository ticketVerificationLogRepository;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_TICKET_PREFIX = "ticket:";
    private static final String BLACKLIST_PREFIX = "blacklist:ticket:";

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
            ticket.markExpired();
            stringRedisTemplate.delete(REDIS_TICKET_PREFIX + ticket.getTicketCode());

            String blacklistKey = BLACKLIST_PREFIX + ticket.getTicketCode();

            LocalDateTime roundEnd = ticket.getReservationSeat()
                    .getSeat().getPerformanceRound().getEndDateTime();

            long ttlSeconds = 3600;

            if(roundEnd != null) {
                long calculated = Duration.between(now, roundEnd.plusDays(1)).getSeconds();

                if(calculated>0) {
                    ttlSeconds = calculated;
                }
            }
                stringRedisTemplate.opsForValue().set(blacklistKey, "true", ttlSeconds, TimeUnit.SECONDS);
        }

        ticketRepository.saveAll(expired);

        List<TicketVerificationLog> logs = expired.stream()
                .map(ticket -> new TicketVerificationLog(
                        ticket.getTicketCode(),
                        "BATCH",
                        "system",
                        TicketVerifyResult.EXPIRED,
                        now
                ))
                .toList();

        ticketVerificationLogRepository.saveAll(logs);

        log.info("만료된 티켓 수: {}", expired.size());

        return RepeatStatus.FINISHED;
    }
}
