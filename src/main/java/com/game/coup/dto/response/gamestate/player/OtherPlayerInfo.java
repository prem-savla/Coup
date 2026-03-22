package com.game.coup.dto.response.gamestate.player;

import java.util.List;


import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OtherPlayerInfo {
    @NonNull
    private final String name;

    private final int coins;

    @NonNull
    private final List<String> revealedCards;
}
