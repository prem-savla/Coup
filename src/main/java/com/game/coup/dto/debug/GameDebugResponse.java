package com.game.coup.dto.debug;

import com.game.coup.domain.Game;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class GameDebugResponse {
    
    private final String currentPlayer;
    private final List<PlayerDebugState> players;

    private GameDebugResponse(Game game) {

        this.players = game.getAlivePlayers()
                .stream()
                .map(PlayerDebugState::new)
                .collect(Collectors.toList());

        currentPlayer = game.getCurrentPlayer().getName();

    }

    public static GameDebugResponse from(Game game) {
        return new GameDebugResponse(game);
    }
}