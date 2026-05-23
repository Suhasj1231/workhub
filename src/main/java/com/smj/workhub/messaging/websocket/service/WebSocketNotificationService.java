package com.smj.workhub.messaging.websocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketNotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(Long userId,
                                 Object payload) {

        String destination = "/queue/notifications-" + userId;

        log.info(
                "WEBSOCKET_NOTIFICATION_DISPATCH_INITIATED | userId={} | destination={}",
                userId,
                destination
        );

        messagingTemplate.convertAndSend(
                destination,
                payload
        );

        log.info(
                "WEBSOCKET_NOTIFICATION_DISPATCHED | userId={} | destination={}",
                userId,
                destination
        );
    }
}
