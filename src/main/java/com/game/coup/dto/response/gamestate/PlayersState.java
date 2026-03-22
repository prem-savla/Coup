package com.game.coup.dto.response.gamestate;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class PlayersState {

    @NonNull
    private final PlayerInfo currentPlayer;

    @NonNull
    private final List<OtherPlayerInfo> otherPlayers;
}
