package com.game.coup.security;

import com.game.coup.service.RoomService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class GameJwtValidationInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final RoomService roomService;
    private final Map<String, Claims> tokenClaimsCache = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        // 1. Check accessor context and ignore DISCONNECT commands to prevent cleanup errors
        if (accessor != null && !StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            
            // FIX: Extract the native "Cookie" header automatically passed by the browser
            String cookieHeader = accessor.getFirstNativeHeader("Cookie");
            
            if (cookieHeader == null) {
                throw new MessageDeliveryException("Access Denied: Missing network session cookies.");
            }
            
            // FIX: Parse out your specific "game_token" from the combined cookie string
            String jwtToken = extractTokenFromCookies(cookieHeader, "game_token");
            if (jwtToken == null) {
                throw new MessageDeliveryException("Access Denied: Game security token not found in cookies.");
            }
            
            Claims claims;

            // 2. High-frequency path protection: Retrieve verified data instantly
            if (tokenClaimsCache.containsKey(jwtToken)) {
                claims = tokenClaimsCache.get(jwtToken);
            } else {
                try {
                    claims = jwtService.extractAllClaims(jwtToken);
                    tokenClaimsCache.put(jwtToken, claims);
                } catch (Exception e) {
                    throw new MessageDeliveryException("Access Denied: Invalid signature.");
                }
            }

            // Note: If your JwtService maps claims explicitly by a key string, 
            // use claims.get("roomId", String.class). If it uses standard subject mapping,
            // adapt this to match how you built it in your token generator method.
            String roomId = claims.get("roomId", String.class);

            // 3. Live Room Validation: Re-verified on every single interaction
            if (!roomService.isRoomActive(roomId)) {
                tokenClaimsCache.remove(jwtToken);
                throw new MessageDeliveryException("Access Denied: The game room no longer exists.");
            }

            String playerName = claims.getSubject();

            // 4. Continually re-bind variables to the active request scope context
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.put("PLAYER_NAME", playerName);
                sessionAttributes.put("ROOM_ID", roomId);
            }
        }
        
        return message;
    }

    /**
     * Helper utility to safely extract a specific cookie value from the raw Cookie header string.
     * Handles formatting variations like multiple spaces or trailing semicolons.
     */
    private String extractTokenFromCookies(String cookieHeader, String targetCookieName) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] pair = cookie.trim().split("=");
            if (pair.length == 2 && pair[0].equals(targetCookieName)) {
                return pair[1];
            }
        }
        return null;
    }
}