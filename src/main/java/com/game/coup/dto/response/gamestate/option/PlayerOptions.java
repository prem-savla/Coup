package com.game.coup.dto.response.gamestate.option;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PlayerOptions {

    private final ActionOption actions;
    private final ResponseOption responses;
    private final RevealOption reveal;
    private final ExchangeOption exchange;

    @Builder(access = AccessLevel.PRIVATE)
    private PlayerOptions(
            ActionOption actions,
            ResponseOption responses,
            RevealOption reveal,
            ExchangeOption exchange
    ) {
        this.actions = actions;
        this.responses = responses;
        this.reveal = reveal;
        this.exchange = exchange;
    }

    public static PlayerOptions forActions(ActionOption actions) {
        return PlayerOptions.builder().actions(actions).build();
    }

    public static PlayerOptions forResponses(ResponseOption responses) {
        return PlayerOptions.builder().responses(responses).build();
    }

    public static PlayerOptions forReveal(RevealOption reveal) {
        return PlayerOptions.builder().reveal(reveal).build();
    }

    public static PlayerOptions forExchange(ExchangeOption exchange) {
        return PlayerOptions.builder().exchange(exchange).build();
    }
}