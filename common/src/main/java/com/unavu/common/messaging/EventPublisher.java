package com.unavu.common.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final StreamBridge streamBridge;

    public boolean publishNotification(Object event) {

        log.info("Publishing notification event {}", event);

        boolean sent = streamBridge.send("notification-events-out-0", event);
        if (!sent) {
            log.error("Failed to publish notification event {}", event);
        }
        return sent;
    }

    public boolean publishFeedEvent(Object event) {
        log.info("Publishing feed event {}", event);
        boolean sent = streamBridge.send("feed-events-out-0", event);
        if (!sent) {
            log.error("Failed to publish feed event {}", event);
        }
        return sent;
    }
    public boolean publishActivityEvent(Object event) {
        log.info("Publishing activity event {}", event);
        boolean sent = streamBridge.send("activity-events-out-0", event);
        if (!sent) {
            log.error("Failed to publish activity event {}", event);
        }
        return sent;
    }
}