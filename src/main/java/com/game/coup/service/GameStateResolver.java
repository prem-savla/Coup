package com.game.coup.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Game;
import com.game.coup.domain.definitions.GamePhase;
import com.game.coup.domain.model.Player;
import com.game.coup.dto.response.gamestate.GameState;
import com.game.coup.dto.response.gamestate.GameStateResponse;
import com.game.coup.dto.response.gamestate.PlayerInfo;
import com.game.coup.dto.response.gamestate.PlayerOptions;
import com.game.coup.dto.response.gamestate.PlayersState;

import lombok.NonNull;

@Service
public class GameStateResolver {

    public GameStateResponse resolve(@NonNull Game game,@NonNull Player viewer) {

        if ( viewer.equals(Player.NONE)) throw new IllegalArgumentException("Invalid player");

        GameState gameState = getGameState(game);
        PlayersState playersState = getPlayersState(game, viewer);
        PlayerOptions playerOptions = getPlayerOptions(game.getGamePhase(), viewer);

        return new GameStateResponse(gameState, playersState, playerOptions);
    
    }
    
    private GameState getGameState(Game game){
        GameState state = GameState.builder()
        .phase(game.getGamePhase())
        .action(game.getTurnContext().getAction())
        .actor(game.getTurnContext().getActor().getName())
        .target(game.getTurnContext().getTarget().getName())
        .challenger(game.getTurnContext().getChallenger().getName())
        .blocker(game.getTurnContext().getBlocker().getName())
        .blockChallenger(game.getTurnContext().getBlockChallenger().getName())
        .build();
        return state;
    }

    private PlayersState getPlayersState(Game game, Player viewer){
        PlayerInfo self = PlayerInfo.builder()
        .name(viewer.getName())
        .coins(viewer.getCoins())
        .revealedCards(List.copyOf(viewer.getRevealedCards()))
        .build();

        List<PlayerInfo> otherPlayers = new ArrayList<>();

        for(Player player : game.getAlivePlayers()){
            if(!player.equals(viewer)){
                PlayerInfo other = PlayerInfo.builder()
                .name(player.getName())
                .coins(player.getCoins())
                .revealedCards(List.copyOf(player.getRevealedCards()))
                .build();
            otherPlayers.add(other);
            }
        }

        return PlayersState.builder()
        .currentPlayer(self)
        .playingCards(List.copyOf(viewer.getPlayingCards()))
        .otherPlayers(otherPlayers)
        .build();
    
    }

    private PlayerOptions getPlayerOptions(GamePhase phase, Player viewer){
        return new PlayerOptions();
    }

}