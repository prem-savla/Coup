package com.game.coup.service;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Game;
import com.game.coup.domain.model.Player;
import com.game.coup.dto.GameStateResponse;

import lombok.NonNull;

@Service
public class GameStateResolver {

    public GameStateResponse resolve(@NonNull Game game,@NonNull Player viewer) {

        if ( viewer.equals(Player.NONE)) throw new IllegalArgumentException("Invalid player");

        switch (game.getGamePhase()) {

            case IDLE:
                return idleState(game, viewer);

            case CHALLENGE_WINDOW:
                return challengeState(game, viewer);

            case BLOCK_WINDOW:
                return blockState(game, viewer);

            case BLOCK_CHALLENGE_WINDOW:
                return blockChallengeState(game, viewer);

            case REVEAL:
                return revealState(game, viewer);

            case EXCHANGE:
                return exchangeState(game, viewer);

            case RESOLVE:
                return resolutionState(game, viewer);

            case GAME_OVER:
                return gameOverState(game, viewer);

            default:
                throw new IllegalStateException("Unknown phase");
        }
    }
    
    private GameStateResponse idleState(Game game, Player viewer){
        return null;
    }

    private GameStateResponse challengeState(Game game, Player viewer){
        return null;
    }

    private GameStateResponse blockState(Game game, Player viewer){
        return null;
    }

    private GameStateResponse blockChallengeState(Game game, Player viewer){
        return null;
    }

    private GameStateResponse revealState(Game game, Player viewer){
        return null;
    }

    private GameStateResponse exchangeState(Game game, Player viewer){
        return null;
    }

    private GameStateResponse resolutionState(Game game, Player viewer){
        return null;
    }

    private GameStateResponse gameOverState(Game game, Player viewer){
        return null;
    }
}