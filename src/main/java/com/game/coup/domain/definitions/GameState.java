package com.game.coup.domain.definitions;

// for frontend abstraction
public enum GameState {
    AWAITING_ACTION,

    CHALLENGE_WINDOW,

    BLOCK_WINDOW,

    BLOCK_CHALLENGE_WINDOW,

    AWAITING_REVEAL,

    AWAITING_EXCHANGE,

    RESOLVING, // placeholder for all internal resolving state

    GAME_OVER   
}