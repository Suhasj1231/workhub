package com.smj.workhub.messaging.websocket.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {

        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(event.getMessage());

        log.info(
                "WEBSOCKET_CLIENT_CONNECTED | sessionId={}",
                headerAccessor.getSessionId()
        );
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(event.getMessage());

        log.info(
                "WEBSOCKET_CLIENT_DISCONNECTED | sessionId={}",
                headerAccessor.getSessionId()
        );
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {

        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(event.getMessage());

        log.info(
                "WEBSOCKET_CLIENT_SUBSCRIBED | sessionId={} | destination={}",
                headerAccessor.getSessionId(),
                headerAccessor.getDestination()
        );
    }
}
