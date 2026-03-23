package com.game.coup.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Game;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.GamePhase;
import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Player;
import com.game.coup.dto.request.GameMoveRequest;
import com.game.coup.dto.response.GameMoveResponse;
import com.game.coup.dto.response.gamestate.option.PlayerOptions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameMoveResolver {
    public static final String INCOME = "INCOME";
    public static final String FOREIGN_AID = "FOREIGN_AID";
    public static final String STEAL = "STEAL";
    public static final String TAX = "TAX";
    public static final String ASSASSINATE = "ASSASSINATE";
    public static final String EXCHANGE = "EXCHANGE";
    public static final String COUP = "COUP";

    // Responses
    public static final String CHALLENGE = "CHALLENGE";
    public static final String BLOCK = "BLOCK";
    public static final String CHALLENGE_BLOCK = "CHALLENGE_BLOCK";
    public static final String PASS = "PASS";

    // Interactions
    public static final String REVEAL = "REVEAL";
    public static final String SWAP_CARDS = "SWAP_CARDS";

    private final GameStateResolver gameStateResolver;
    
    public GameMoveResponse resolve(Game game,  GameMoveRequest request) {
        Player viewer = game.getPlayerByName(request.getPlayerName());
        PlayerOptions options = gameStateResolver.getPlayerOptions(game, viewer);
        GamePhase phase = game.getGamePhase();
        String choice = request.getChoice();
        String target = request.getTarget();
        Card revealCard = request.getRevealCard();
        List<Card> drawnCards = request.getDrawnCards();
        List<Card> returnedCards = request.getReturnedCards();


        switch (choice) {
            case INCOME:
                if(options.getActions().getValidNonTargetedActions().contains(INCOME)) game.startAction(viewer, ActionType.INCOME);
                break;
            case FOREIGN_AID:
                if(options.getActions().getValidNonTargetedActions().contains(FOREIGN_AID)) game.startAction(viewer, ActionType.FOREIGN_AID);
                break;
            case STEAL:
                if(options.getActions().getValidNonTargetedActions().contains(STEAL) 
                    && options.getActions().getValidTargets().contains(target)) 
                game.startTargetedAction(viewer, game.getPlayerByName(target), ActionType.STEAL);
                break;
            case TAX:
                if(options.getActions().getValidNonTargetedActions().contains(TAX))  game.startAction(viewer, ActionType.TAX);
                break;
            case ASSASSINATE:
                if(options.getActions().getValidNonTargetedActions().contains(ASSASSINATE) 
                    && options.getActions().getValidTargets().contains(target)) 
                game.startTargetedAction(viewer, game.getPlayerByName(target), ActionType.ASSASSINATE);
                break;
            case EXCHANGE:
                if(options.getActions().getValidNonTargetedActions().contains(EXCHANGE)) game.startAction(viewer, ActionType.EXCHANGE);
                break;
            case COUP:
                if(options.getActions().getValidNonTargetedActions().contains(COUP) 
                    && options.getActions().getValidTargets().contains(target)) 
                game.startTargetedAction(viewer, game.getPlayerByName(target), ActionType.COUP);
                break;
            case CHALLENGE:
                if(options.getResponses().getValidResponses().contains(CHALLENGE)) game.challengeAction(viewer);
            case BLOCK:
                if(options.getResponses().getValidResponses().contains(BLOCK)) game.blockAction(viewer);
                break;
            case CHALLENGE_BLOCK:
                if(options.getResponses().getValidResponses().contains(CHALLENGE_BLOCK)) game.challengeBlockAction(viewer);
                break;
            case SWAP_CARDS:
                if(exchangeHelper(options,viewer,game,drawnCards,returnedCards)) game.executeExchange(drawnCards, returnedCards);
                break;
            case REVEAL:
                if(options.getReveal().getValidCards().contains(revealCard)) game.executeReveal(viewer, revealCard);
                break;
            case PASS:
                // if all pass then only move to next state
                break;
        
            default:
                break;
        }

        
        return null;

    }

    // --- helper ---
    private boolean exchangeHelper(PlayerOptions options, Player viewer, Game game, List<Card> drawnCards, List<Card> returnedCards){
        
        return true;
    }
    
    /// need to verify target  other list non null 
}
