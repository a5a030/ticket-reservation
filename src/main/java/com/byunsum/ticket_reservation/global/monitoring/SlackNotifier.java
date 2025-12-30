package com.byunsum.ticket_reservation.global.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class SlackNotifier {
    private static final Logger log = LoggerFactory.getLogger(SlackNotifier.class);

    @Value("${slack.webhook.url:}")
    private String webhookUrl;

    @Value("${slack.enabled:false}")
    private boolean slackEnabled;

    @Value("${slack.http.request-timeout-ms:3000}")
    private long requestTimeoutMs;

    @Value("${slack.log.missing-webhook-level:debug}")
    private String missingWebhookLogLevel;

    private final HttpClient httpClient;

    public SlackNotifier(@Value("${slack.http.connect-timeout-ms:2000}") long connectTimeoutMs) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();
    }

    public void send(String message) {
        if(!slackEnabled) {
            if(log.isDebugEnabled()) {
                log.debug("Slack 알림 비활성화(slack.enabled=false). 메시지 전송 생략: {}", message);
            }
            return;
        }

        if(webhookUrl == null || webhookUrl.isBlank()) {
            logMissingWebhook(message);
            return;
        }

        try {
            String safe = escapeJson(truncate(message));
            String payload = "{\"text\":\"" + safe + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .timeout(java.time.Duration.ofMillis(requestTimeoutMs))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> {
                            int code = response.statusCode();
                            if(code >= 200 && code < 300) {
                                log.debug("Slack 전송 성공: {}", code);
                            } else {
                                log.warn("Slack 전송 실패: status={}, webhook={}", code, maskWebhook(webhookUrl));
                            }
            })
                    .exceptionally(ex -> {
                        log.error("Slack 전송 async 예외", ex);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Slack 알림 전송 실패", e);
        }
    }

    private String escapeJson(String message) {
        if(message == null) return "";
        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private void logMissingWebhook(String message) {
        if("warn".equalsIgnoreCase(missingWebhookLogLevel)) {
            log.warn("Slack Webhook URL이 설정되지 않음. 메시지 전송 생략: {}", message);
        } else {
            if(log.isDebugEnabled()) {
                log.debug("Slack Webhook URL이 설정되지 않음. 메시지 전송 생략: {}", message);
            }
        }
    }

    private String maskWebhook(String url) {
        if(url == null) return "";
        int index = url.lastIndexOf("/");
        return (index > 0) ? url.substring(0, index) + "/***" : "***";
    }

    private String truncate(String message) {
        if (message == null) return "";
        return message.length() > 1500 ? message.substring(0, 1500) + "...(truncated)" : message;
    }
}
