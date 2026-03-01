package com.game.coup.dto.debug;

import com.game.coup.domain.Game;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class GameDebugResponse {

    private final List<PlayerDebug> players;

    private GameDebugResponse(Game game) {

        this.players = game.getAlivePlayers()
                .stream()
                .map(PlayerDebug::new)
                .collect(Collectors.toList());

    }

    public static GameDebugResponse from(Game game) {
        return new GameDebugResponse(game);
    }
}