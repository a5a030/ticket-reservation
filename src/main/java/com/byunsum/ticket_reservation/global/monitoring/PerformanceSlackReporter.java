package com.byunsum.ticket_reservation.global.monitoring;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PerformanceSlackReporter {
    private final SlackNotifier slackNotifier;
    private final PerformanceRepository performanceRepository;
    private final TicketRepository ticketRepository;

    public PerformanceSlackReporter(SlackNotifier slackNotifier, PerformanceRepository performanceRepository, TicketRepository ticketRepository) {
        this.slackNotifier = slackNotifier;
        this.performanceRepository = performanceRepository;
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

    // 30분 단위 진행률 리포트
    @Scheduled(cron = "0 */30 * * * *")
    public void reportProgressEvery30Min() {
        LocalDateTime now = LocalDateTime.now();
        List<Performance> performances = performanceRepository.findByEntryStartTimeBetween(now.minusHours(3), now.plusHours(3));

        for (Performance performance : performances) {
            LocalDateTime entryStart = performance.getEntryStartTime();

            if(entryStart!=null && now.isAfter(entryStart) && performance.getEndDateTime().isAfter(now)) {
                long total = ticketRepository.countByPerformance(performance);
                long entered = ticketRepository.countByPerformanceAndEnteredTrue(performance);
                double ratio = total > 0 ? (entered * 100.0 / total) : 0;

                String message = String.format(
                        "📊 검표 진행 현황\n- 공연명: %s\n- 회차: %s\n- 총 예매: %d명\n- 입장 완료: %d명 (%.1f%%)\n- 미입장: %d명\n- 마지막 갱신: %s",
                        performance.getTitle(),
                        performance.getRounds(),
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
        List<Performance> performances = performanceRepository.findByEntryStartTimeBetween(now.minusMinutes(11), now.plusMinutes(1));

        for (Performance performance : performances) {
            LocalDateTime start = performance.getStartDateTime();

            if(start.minusMinutes(10).equals(now)) {
                long total = ticketRepository.countByPerformance(performance);
                long entered = ticketRepository.countByPerformanceAndEnteredTrue(performance);
                double ratio = total > 0 ? (entered * 100.0 / total) : 0;

                slackNotifier.send(String.format(
                        "⏰ 공연 시작 10분 전\n- 공연명: %s\n- 현재 입장률: %.1f%%",
                        performance.getTitle(), ratio
                ));
            }

            if(start.equals(now)) {
                long total = ticketRepository.countByPerformance(performance);
                long entered = ticketRepository.countByPerformanceAndEnteredTrue(performance);
                double ratio = total > 0 ? (entered * 100.0 / total) : 0;

                slackNotifier.send(String.format(
                        "🎶 공연 시작\n- 공연명: %s\n- 최종 입장률: %.1f%%",
                        performance.getTitle(), ratio
                ));
            }
        }
    }
}
