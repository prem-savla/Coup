package com.game.coup.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.game.coup.security.GameJwtValidationInterceptor;

import org.springframework.lang.NonNull;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final GameJwtValidationInterceptor jwtValidationInterceptor;

    public WebSocketConfig(GameJwtValidationInterceptor jwtValidationInterceptor) {
        this.jwtValidationInterceptor = jwtValidationInterceptor;
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        //  later you'll use these like:
        //  /topic/game/room123        → broadcast to all players in room
        
        config.setApplicationDestinationPrefixes("/app");
        //  later your controller will have:
        //  @MessageMapping("/move")   → client sends to /app/move
        //  @MessageMapping("/state")  → client sends to /app/state
        
        config.setUserDestinationPrefix("/user");
        //  later you'll use these like:
        //  /user/queue/game/errors    → send private message to the current user
        //  in your controller or service:
        //  simpMessagingTemplate.convertAndSendToUser("Alice", "/queue/game/errors", msg);
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        // The URL all players connect to — same for everyone

        .setAllowedOriginPatterns("*")
        // Which domains are allowed to connect
        // "*" = any frontend (localhost, your game website, etc.)

        .withSockJS();
        // If a player's browser doesn't support WebSocket
        // SockJS automatically falls back to HTTP polling
        // Player still works — they just don't know the difference
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        // Intercepts the inbound STOMP channel directly
        registration.interceptors(jwtValidationInterceptor);
    }
}