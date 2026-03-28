package com.game.coup.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Game;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.GamePhase;
import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Player;
import com.game.coup.dto.request.GameMoveRequest;
import com.game.coup.dto.response.gamestate.GameStateResponse;
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
    
    public GameStateResponse resolve(Game game,  GameMoveRequest request) {
        Player viewer = game.getPlayerByName(request.getPlayerName());
        PlayerOptions options = gameStateResolver.getPlayerOptions(game, viewer);
        int count = options.getExchange()!=null?options.getExchange().getSelectCount():0;
        GamePhase phase = game.getGamePhase();

        String choice = request.getChoice();
        String target = request.getTarget();
        Card revealCard = request.getRevealCard();
        List<Card> cardsKept = request.getCardsKept();
        List<Card> cardsReturned = request.getCardsReturned();


        boolean executed = false;

        switch (choice) {
            case INCOME:
                if(options.getActions().getValidNonTargetedActions().contains(INCOME)) {
                    game.startAction(viewer, ActionType.INCOME);
                    executed = true;
                }
                break;
            case FOREIGN_AID:
                if(options.getActions().getValidNonTargetedActions().contains(FOREIGN_AID)){ 
                    game.startAction(viewer, ActionType.FOREIGN_AID);
                    executed = true;
                }
                break;
            case STEAL:
                if(options.getActions().getValidTargetedActions().contains(STEAL) 
                 && options.getActions().getValidTargets().contains(target)) {
                    game.startTargetedAction(viewer, game.getPlayerByName(target), ActionType.STEAL);
                    executed = true;
                }
                break;
            case TAX:
                if(options.getActions().getValidNonTargetedActions().contains(TAX)){
                    game.startAction(viewer, ActionType.TAX);
                    executed = true;
                }
                break;
            case ASSASSINATE:
                if(options.getActions().getValidTargetedActions().contains(ASSASSINATE) 
                && options.getActions().getValidTargets().contains(target)){ 
                    game.startTargetedAction(viewer, game.getPlayerByName(target), ActionType.ASSASSINATE);
                    executed = true;
                }
                break;
            case EXCHANGE:
                if(options.getActions().getValidNonTargetedActions().contains(EXCHANGE)) {
                    game.startAction(viewer, ActionType.EXCHANGE);
                    executed = true;
                }
                break;
            case COUP:
                if(options.getActions().getValidTargetedActions().contains(COUP) 
                && options.getActions().getValidTargets().contains(target)){
                    game.startTargetedAction(viewer, game.getPlayerByName(target), ActionType.COUP);
                    executed = true;
                }
                break;
            case CHALLENGE:
                if(options.getResponses().getValidResponses().contains(CHALLENGE)){
                    game.challengeAction(viewer);
                    executed = true;
                }
                break;
            case BLOCK:
                if(options.getResponses().getValidResponses().contains(BLOCK)){
                    game.blockAction(viewer);
                    executed = true;
                }
                break;
            case CHALLENGE_BLOCK:
                if(options.getResponses().getValidResponses().contains(CHALLENGE_BLOCK)){
                    game.challengeBlockAction(viewer);
                    executed = true;
                }
                break;
            case SWAP_CARDS:
                if(options.getResponses().getValidResponses().contains(SWAP_CARDS) &&cardsKept.size()<=count) {
                    game.executeExchange(cardsKept, cardsReturned);
                    executed = true;
                }
                break;
            case REVEAL:
                if(options.getResponses().getValidResponses().contains(REVEAL)  && options.getReveal().getValidCards().contains(revealCard)) {
                    game.executeReveal(viewer, revealCard);
                    executed = true;
                }
                break;
            case PASS:
                if (options.getResponses().getValidResponses().contains(PASS)) {
                    switch (phase) {
                        case CHALLENGE_WINDOW       -> game.noChallengeAction(viewer);
                        case BLOCK_WINDOW           -> game.noBlockAction(viewer);
                        case BLOCK_CHALLENGE_WINDOW -> game.noChallengeBlockAction(viewer);
                        default -> throw new IllegalStateException("Unhandled phase: " + phase);
                    }
                    executed = true;
               }
                break;    
            default:
                throw new IllegalStateException("Unhandeled State: "+ choice);
        }

        if (!executed) throw new IllegalArgumentException("Invalid move: " + choice);
        
        return gameStateResolver.resolve(game, viewer);

    }

}