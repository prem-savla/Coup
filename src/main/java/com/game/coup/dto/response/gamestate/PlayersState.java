package com.game.coup.dto.response.gamestate;

import java.util.List;

import com.game.coup.domain.model.Card;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class PlayersState {

    @NonNull
    private final PlayerInfo currentPlayer;

    @NonNull
    private final List<Card> playingCards;

    @NonNull
    private final List<PlayerInfo> otherPlayers;
}
