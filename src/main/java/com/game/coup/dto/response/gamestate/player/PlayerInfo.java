package com.game.coup.dto.response.gamestate.player;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class PlayerInfo {

    @NonNull
    private final String name;

    private final int coins;

    private final boolean alive;

    @NonNull
    private final List<String> playingCards;

    @NonNull
    private final List<String> revealedCards;
}