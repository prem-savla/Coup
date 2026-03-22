package com.game.coup.controller;

import com.game.coup.dto.request.RoomRequest;
import com.game.coup.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/room")
    public ResponseEntity<?> handleRoom(@RequestBody RoomRequest request) {

        // Basic validation
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

                return ResponseEntity.ok(
                        Map.of(
                                "roomId", roomId,
                                "message", "Room created successfully."
                        )
                );
            }

            case JOIN -> {
                if (request.getRoomId() == null || request.getRoomId().isBlank()) {
                    return ResponseEntity.badRequest()
                            .body("roomId is required for JOIN action.");
                }

                roomService.joinRoom(
                        request.getRoomId(),
                        request.getPlayerName()
                );

                return ResponseEntity.ok(
                        Map.of(
                                "roomId", request.getRoomId(),
                                "message", "Joined room successfully."
                        )
                );
            }

            case START -> {
                if (request.getRoomId() == null || request.getRoomId().isBlank()) {
                    return ResponseEntity.badRequest()
                            .body("roomId is required for JOIN action.");
                }

                roomService.startGame(
                        request.getRoomId(),
                        request.getPlayerName()
                );

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
}
