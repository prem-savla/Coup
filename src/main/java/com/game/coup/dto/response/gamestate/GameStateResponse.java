package com.game.coup.dto.response.gamestate;

import com.game.coup.dto.response.gamestate.game.GameState;
import com.game.coup.dto.response.gamestate.option.PlayerOptions;
import com.game.coup.dto.response.gamestate.player.PlayersState;

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

