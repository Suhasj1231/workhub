package com.smj.workhub.messaging.websocket.controller;

import com.smj.workhub.messaging.websocket.service.WebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket")
public class WebSocketTestController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketTestController.class);

    private final WebSocketNotificationService webSocketNotificationService;

    public WebSocketTestController(WebSocketNotificationService webSocketNotificationService) {
        this.webSocketNotificationService = webSocketNotificationService;
    }

    @PostMapping("/test-notification")
    public String sendTestNotification(@RequestParam Long userId,
                                       @RequestParam String message) {

        Map<String, Object> payload = new HashMap<>();

        payload.put("type", "TEST_NOTIFICATION");
        payload.put("message", message);
        payload.put("timestamp", Instant.now().toString());

        log.info(
                "WEBSOCKET_TEST_NOTIFICATION_TRIGGERED | userId={} | message={}",
                userId,
                message
        );

        webSocketNotificationService.sendNotification(
                userId,
                payload
        );

        return "Realtime notification pushed successfully";
    }
}
