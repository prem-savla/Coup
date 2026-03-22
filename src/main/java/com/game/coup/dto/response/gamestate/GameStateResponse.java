package com.game.coup.dto.response.gamestate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class GameStateResponse {  
    @NonNull private final GameState gameState;
    @NonNull private final PlayersState playersState;
    @NonNull private final PlayerOptions playerOptions;
}

