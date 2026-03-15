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

        return streamBridge.send("notification-events-out-0", event);
    }
}