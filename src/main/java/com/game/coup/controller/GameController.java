package com.game.coup.controller;

import com.game.coup.dto.debug.GameDebugResponse;
import com.game.coup.dto.request.GameMoveRequest;
import com.game.coup.dto.request.GameStateRequest;
import com.game.coup.dto.response.GameMoveResponse;
import com.game.coup.dto.response.gamestate.GameStateResponse;
import com.game.coup.service.GameService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/{roomId}/state")
    public GameStateResponse getGameState(
            @PathVariable String roomId,
            @Valid @RequestBody GameStateRequest request
    ) {
        return gameService.getGameState(roomId, request);
    }

    @PostMapping("/{roomId}/move")
    public GameMoveResponse processMove(
            @PathVariable String roomId,
            @Valid @RequestBody GameMoveRequest request
    ) {
        return gameService.processGameMove(roomId, request);
    }

    @GetMapping("/{roomId}/debug")
    public GameDebugResponse debug(@PathVariable String roomId) {
        return gameService.getDebugState(roomId);
    }
}