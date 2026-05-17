package com.game.coup.controller;

import com.game.coup.dto.request.RoomRequest;
import com.game.coup.service.RoomService;
import com.game.coup.security.JwtService; // Added import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(
    origins = "http://localhost:3000", // Change this to your exact frontend URL (e.g., your React/Vue dev server URL)
    allowCredentials = "true"          // Tells the browser it is allowed to read Set-Cookie headers
)
@RequestMapping("/api")
@RequiredArgsConstructor 
public class RoomController {

    private final RoomService roomService;
    private final JwtService jwtService; // Injected dependency

    @PostMapping("/room")
    public ResponseEntity<?> handleRoom(@RequestBody RoomRequest request) {

        if (request == null ||
            request.getAction() == null ||
            request.getPlayerName() == null ||
            request.getPlayerName().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Action and playerName are required.");
        }

        switch (request.getAction()) {

            case CREATE -> {
                String roomId = roomService.createRoom(request.getPlayerName());
                
                // 1. Generate the token
                String token = jwtService.generateToken(request.getPlayerName(), roomId);
                // 2. Build the cookie
                ResponseCookie cookie = buildGameCookie(token);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(Map.of(
                                "roomId", roomId,
                                "message", "Room created successfully."
                        ));
            }

            case JOIN -> {
                if (request.getRoomId() == null || request.getRoomId().isBlank()) {
                    return ResponseEntity.badRequest()
                            .body("roomId is required for JOIN action.");
                }

                roomService.joinRoom(request.getRoomId(), request.getPlayerName());
                
                // 1. Generate the token
                String token = jwtService.generateToken(request.getPlayerName(), request.getRoomId());
                // 2. Build the cookie
                ResponseCookie cookie = buildGameCookie(token);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(Map.of(
                                "roomId", request.getRoomId(),
                                "message", "Joined room successfully."
                        ));
            }

            case START -> {
                if (request.getRoomId() == null || request.getRoomId().isBlank()) {
                    return ResponseEntity.badRequest()
                            .body("roomId is required for START action."); 
                }

                roomService.startGame(request.getRoomId(), request.getPlayerName());

                return ResponseEntity.ok(
                        Map.of(
                                "roomId", request.getRoomId(),
                                "message", "Game Started."
                        )
                );
            }
        }

        return ResponseEntity.badRequest().build();
    }

    /**
     * Reusable helper method to construct your security-hardened cookie configuration
     */
    private ResponseCookie buildGameCookie(String token) {
        return ResponseCookie.from("game_token", token)
                .httpOnly(true)
                .secure(false) // Set to true when deploying over real HTTPS
                .path("/")
                .sameSite("Strict")
                .maxAge(24 * 60 * 60)
                .build();
    }
}