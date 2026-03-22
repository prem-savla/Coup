package com.game.coup.dto.response.gamestate;

import java.util.List;

import com.game.coup.domain.model.Card;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class PlayerInfo {

    @NonNull
    private final String name;

    private final int coins;

    @NonNull
    private final List<Card> revealedCards;
}