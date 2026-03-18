package com.game.coup.domain.definitions;

public enum GameState {
    AWAITING_ACTION,

    AWAITING_RESPONSE, // block or challenge

    AWAITING_CHALLENGE_BLOCK,

    AWAITING_REVEAL,

    AWAITING_EXCHANGE,

    GAME_OVER   
}