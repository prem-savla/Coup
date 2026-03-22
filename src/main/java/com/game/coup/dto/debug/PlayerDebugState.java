
package com.game.coup.dto.debug;

import java.util.List;

import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Player;

import lombok.Getter;

@Getter
class PlayerDebugState {

    private final String name;
    private final int coins;
    private final List<Card> hiddenCards; 
    private final List<Card> revealedCards;

    PlayerDebugState(Player player) {
        this.name = player.getName();
        this.coins = player.getCoins();
        this.hiddenCards = List.copyOf(player.getPlayingCards());
        this.revealedCards = List.copyOf(player.getRevealedCards());
    }
}