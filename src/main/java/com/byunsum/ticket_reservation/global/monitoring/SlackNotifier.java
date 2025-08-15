package com.byunsum.ticket_reservation.global.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class SlackNotifier {
    private static Logger log = LoggerFactory.getLogger(SlackNotifier.class);

    @Value("${slack.webhook.url:}")
    private String webhookUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void send(String message) {
        if(webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack Webhook URL이 설정되지 않음. 메시지 전송 생략: {}", message);
            return;
        }

        try {
            String payload = "{\"text\":\"" + message.replace("\"","\\\"") + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> log.debug("Slack 응답: {}", response.statusCode()));
        } catch (Exception e) {
            log.error("Slack 알림 전송 실패", e);
        }
    }
}
