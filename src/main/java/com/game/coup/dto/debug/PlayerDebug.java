
package com.game.coup.dto.debug;

import com.game.coup.domain.model.Player;

import lombok.Getter;

@Getter
class PlayerDebug {

    private final String name;
    private final int coins;
    private final Object hiddenCards; 
    private final Object revealedCards;

    PlayerDebug(Player player) {
        this.name = player.getName();
        this.coins = player.getCoins();
        this.revealedCards = player.getRevealedCards();
        this.hiddenCards = player.getPlayingCards();
    }
}