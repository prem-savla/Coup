package com.game.coup.dto.response.gamestate.game;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.GamePhase;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GameState {

    @NonNull
    private final GamePhase phase;

    private final ActionType action;
    private final String actor;
    private final String target;
    private final String challenger;
    private final String blocker;
    private final String blockChallenger;
}