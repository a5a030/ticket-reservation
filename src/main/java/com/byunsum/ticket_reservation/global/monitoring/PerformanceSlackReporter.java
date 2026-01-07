package com.byunsum.ticket_reservation.global.monitoring;

import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRoundRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PerformanceSlackReporter {
    private final SlackNotifier slackNotifier;
    private final PerformanceRoundRepository roundRepository;
    private final TicketRepository ticketRepository;

    public PerformanceSlackReporter(SlackNotifier slackNotifier, PerformanceRoundRepository roundRepository, TicketRepository ticketRepository) {
        this.slackNotifier = slackNotifier;
        this.roundRepository = roundRepository;
        this.ticketRepository = ticketRepository;
    }

    // 검표 실패 -> 서비스 로직에서 직접 호출
    public void notifyTicketVerificationFail(String performanceTitle, String ticketCode, String reason) {
        String message = String.format(
                "🚨 검표 실패\n- 공연명: %s\n- 티켓 코드: %s\n- 원인: %s\n- 시각: %s",
                performanceTitle, ticketCode, reason, LocalDateTime.now()
        );

        slackNotifier.send(message);
    }

    // 30분 단위 입장률 리포트
    @Scheduled(cron = "0 */30 * * * *")
    public void reportProgressEvery30Min() {
        LocalDateTime now = LocalDateTime.now();
        List<PerformanceRound> rounds = roundRepository.findByEntryDateTimeBetween(now.minusHours(3), now.plusHours(3));

        for (PerformanceRound round : rounds) {
            LocalDateTime entryStart = round.getEntryDateTime();
            LocalDateTime end = round.getEndDateTime();

            if(entryStart == null || end == null) continue;

            if(now.isAfter(entryStart) && end.isAfter(now)) {
                long total = ticketRepository.countByPerformanceRound(round);
                long entered = ticketRepository.countByPerformanceRoundAndEnteredTrue(round);
                double ratio = total > 0 ? (entered * 100.0 / total) : 0;

                String message = String.format(
                        "📊 검표 진행 현황\n- 공연명: %s\n- 회차: %d\n- 총 예매: %d명\n- 입장 완료: %d명 (%.1f%%)\n- 미입장: %d명\n- 마지막 갱신: %s",
                        round.getPerformance().getTitle(),
                        round.getRoundNumber(),
                        total,
                        entered,
                        ratio,
                        total - entered,
                        now
                );

                slackNotifier.send(message);
            }
        }
    }

    // 공연 시작 10분 전&정각 알림
    @Scheduled(cron = "0 */1 * * * *")
    public void notifyPerformanceStart() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        List<PerformanceRound> rounds =
                roundRepository.findByStartDateTimeBetween(now.minusMinutes(11), now.plusMinutes(1));

        for (PerformanceRound round : rounds) {
            LocalDateTime start = round.getStartDateTime();

            if(start == null) continue;

            if(start.minusMinutes(10).equals(now)) {
                long total = ticketRepository.countByPerformanceRound(round);
                long entered = ticketRepository.countByPerformanceRoundAndEnteredTrue(round);
                double ratio = total > 0 ? (entered * 100.0 / total) : 0;

                slackNotifier.send(String.format(
                        "⏰ 공연 시작 10분 전\n- 공연명: %s\n- 회차: %d\n- 현재 입장률: %.1f%%",
                        round.getPerformance().getTitle(),
                        round.getRoundNumber(),
                        ratio
                ));
            }

            if(start.equals(now)) {
                long total = ticketRepository.countByPerformanceRound(round);
                long entered = ticketRepository.countByPerformanceRoundAndEnteredTrue(round);
                double ratio = total > 0 ? (entered * 100.0 / total) : 0;

                slackNotifier.send(String.format(
                        "🎶 공연 시작\n- 공연명: %s\n- 회차: %d\n- 현재 입장률: %.1f%%",
                        round.getPerformance().getTitle(),
                        round.getRoundNumber(),
                        ratio
                ));
            }
        }
    }
}
