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
import com.game.coup.domain.turn.TurnContext;
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

        if (viewer.equals(Player.NONE)) throw new IllegalArgumentException("Invalid player");

        GameState gameState = getGameState(game);
        PlayersState playersState = getPlayersState(game, viewer);
        PlayerOptions playerOptions = getPlayerOptions(game, viewer);

        return new GameStateResponse(gameState, playersState, playerOptions);
    
    }
    
    private GameState getGameState(Game game){
        if(game.getGamePhase() == GamePhase.IDLE){
            GameState state = GameState.builder()
            .currentPlayer(game.getCurrentPlayer().getName())
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
        .self(self)
        .otherPlayers(otherPlayers)
        .build();
    
    }

    public PlayerOptions getPlayerOptions(Game game, Player viewer) {
        GamePhase phase = game.getGamePhase();

        switch (phase) {

            case IDLE:
                return viewer.equals(game.getCurrentPlayer())?
                PlayerOptions.forActions(buildActionOptions(game, viewer)):
                PlayerOptions.blankOption();

            case CHALLENGE_WINDOW:
                return PlayerOptions.forResponses(buildChallengeResponses());

            case BLOCK_WINDOW:{
                    TurnContext ctx = game.getTurnContext();
                    ActionType action = ctx.getAction();
                    Player blocker = ctx.getBlocker();

                    if(action.equals(ActionType.STEAL) || action.equals(ActionType.ASSASSINATE))
                        return viewer.equals(blocker)?
                        PlayerOptions.forResponses(buildBlockResponses()):
                        PlayerOptions.blankOption();
                    else if(action.equals(ActionType.FOREIGN_AID))
                        return PlayerOptions.forResponses(buildBlockResponses());
                    else
                        throw new IllegalStateException("Wrong action for block window:");
                }
                
            case BLOCK_CHALLENGE_WINDOW:
                return PlayerOptions.forResponses(buildBlockChallengeResponses());

            case REVEAL:
                return viewer.equals(game.getRevealPlayer())?
                PlayerOptions.forReveal(buildRevealOption(viewer)):
                PlayerOptions.blankOption();
            case EXCHANGE:
                return viewer.equals(game.getCurrentPlayer())?
                PlayerOptions.forExchange(buildExchangeOption(game, viewer)):
                PlayerOptions.blankOption();

            case RESOLVE:
                PlayerOptions.blankOption();

            case GAME_OVER:
                PlayerOptions.blankOption();

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

        List<String> nonTargetedActions = validActions.stream()
            .filter(a -> !ActionType.valueOf(a).targeted)
            .toList();

        List<String> validTargetedActions = validActions.stream()
            .filter(a -> ActionType.valueOf(a).targeted)
            .toList();

        List<String> targets = getPlayerView(game.getAlivePlayers().stream()
            .filter(p -> p != viewer)
            .toList());

        return ActionOption.builder()
        .validNonTargetedActions(nonTargetedActions)
        .validTargetedActions(validTargetedActions)
        .validTargets(targets)
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
        .validCards(viewer.getPlayingCards())
        .build();
    }

     private ExchangeOption buildExchangeOption(Game game, Player viewer){
        return ExchangeOption.builder()
        .drawnCards(game.getExchangeDrawnCards())
        .playingCards(viewer.getPlayingCards())
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