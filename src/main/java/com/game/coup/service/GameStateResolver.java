package com.game.coup.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Game;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.GamePhase;
import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Player;
import com.game.coup.dto.response.gamestate.GameStateResponse;
import com.game.coup.dto.response.gamestate.game.GameState;
import com.game.coup.dto.response.gamestate.option.ActionOption;
import com.game.coup.dto.response.gamestate.option.ExchangeOption;
import com.game.coup.dto.response.gamestate.option.PlayerOptions;
import com.game.coup.dto.response.gamestate.option.ResponseOption;
import com.game.coup.dto.response.gamestate.option.RevealOption;
import com.game.coup.dto.response.gamestate.player.OtherPlayerInfo;
import com.game.coup.dto.response.gamestate.player.PlayerInfo;
import com.game.coup.dto.response.gamestate.player.PlayersState;

import lombok.NonNull;

@Service
public class GameStateResolver {

    public GameStateResponse resolve(@NonNull Game game,@NonNull Player viewer) {

        if ( viewer.equals(Player.NONE)) throw new IllegalArgumentException("Invalid player");

        GameState gameState = getGameState(game);
        PlayersState playersState = getPlayersState(game, viewer);
        PlayerOptions playerOptions = getPlayerOptions(game, viewer);

        return new GameStateResponse(gameState, playersState, playerOptions);
    
    }
    
    private GameState getGameState(Game game){
        if(game.getGamePhase() == GamePhase.IDLE){
            GameState state = GameState.builder()
            .phase(game.getGamePhase())
            .build();
            return state;
        }
        
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
        .playingCards(getCardsView(viewer.getPlayingCards()))
        .revealedCards(getCardsView(viewer.getRevealedCards()))
        .build();

        List<OtherPlayerInfo> otherPlayers = new ArrayList<>();

        for(Player player : game.getAlivePlayers()){
            if(!player.equals(viewer)){
                OtherPlayerInfo other = OtherPlayerInfo.builder()
                .name(player.getName())
                .coins(player.getCoins())
                .revealedCards(getCardsView(player.getRevealedCards()))
                .build();
            otherPlayers.add(other);
            }
        }

        return PlayersState.builder()
        .currentPlayer(self)
        .otherPlayers(otherPlayers)
        .build();
    
    }

    private PlayerOptions getPlayerOptions(Game game, Player viewer) {
        GamePhase phase = game.getGamePhase();

        switch (phase) {

            case IDLE:
                return PlayerOptions.forActions(buildActionOptions(game, viewer));

            case CHALLENGE_WINDOW:
                return PlayerOptions.forResponses(buildChallengeResponses());

            case BLOCK_WINDOW:
                return PlayerOptions.forResponses(buildBlockResponses());

            case BLOCK_CHALLENGE_WINDOW:
                return PlayerOptions.forResponses(buildBlockChallengeResponses());

            case REVEAL:
                return PlayerOptions.forReveal(buildRevealOption(viewer));

            case EXCHANGE:
                return PlayerOptions.forExchange(buildExchangeOption(game, viewer));

            case RESOLVE:
                return null;

            case GAME_OVER:
                return null; // need to handle this & winner

            default:
                throw new IllegalStateException("Unhandled phase: " + phase);
        }
    }

    // --- helper methods ---

    private ActionOption buildActionOptions(Game game, Player viewer) {

        List<String> validActions = viewer.getCoins()>=10 
        ?List.of(ActionType.COUP.name()) 
        :Arrays.stream(ActionType.values())
        .filter(a -> viewer.getCoins() >= a.cost)
        .map(ActionType::name)
        .toList();

        List<String> validTargets = getPlayerView(game.getAlivePlayers());// remove self 

        return ActionOption.builder()
        .validActions(validActions)
        .validTargets(validTargets)
        .build();
    }

    private ResponseOption buildChallengeResponses(){
        return ResponseOption.builder()
        .validResponses(List.of("CHALLENGE", "PASS"))
        .build();
    }

    private ResponseOption buildBlockResponses(){
        return ResponseOption.builder()
        .validResponses(List.of("BLOCK", "PASS"))
        .build();
    }

    private ResponseOption buildBlockChallengeResponses(){
        return ResponseOption.builder()
        .validResponses(List.of("CHALLENGE_BLOCK", "PASS"))
        .build();
    }

    private RevealOption buildRevealOption(Player viewer){
        return RevealOption.builder()
        .validCards(getCardsView(viewer.getPlayingCards()))
        .build();
    }

     private ExchangeOption buildExchangeOption(Game game, Player viewer){
        return ExchangeOption.builder()
        .drawnCards(null)
        .playingCards(getCardsView(viewer.getPlayingCards()))
        .selectCount(viewer.getPlayingCards().size())
        .build();
    }

    // ---  utils ---

    public List<String> getCardsView(List<Card> cards){
        return cards.stream()
        .map(card -> card.getType().toString())
        .toList();
    }

    public List<String> getPlayerView(List<Player> players){
        return players.stream()
        .map(player -> player.getName())
        .toList();
    }

}