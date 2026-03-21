package com.game.coup.domain.turn;

import java.util.List;
import java.util.Objects;

import com.game.coup.domain.Game;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;

public class Executioner {

    // This class is a helper for thr game class that abstracts few complex functions
    private Executioner() {
        throw new AssertionError("Utility class");
    }

    public static void executeChallenge(Game game){
        TurnContext ctx = Objects.requireNonNull(game.getTurnContext(), "Context is not initialized");
        ActionType action = ctx.getAction();
        Player actor = ctx.getActor();
        Player challenger = ctx.getChallenger();

        if(challenger.equals(Player.NONE)) throw new IllegalStateException("Challenger not set");
    
        for(Card card: actor.getPlayingCards()){
            if(card.getType().canPerform(action)){ 
                ctx.setActionChallengeLoser(challenger);
                List<Card> cardsDrawn = game.getDeck().dealCards(1);
                List<Card> cardsReturned = List.of(card);
                actor.exchangeCards(cardsDrawn, cardsReturned);
                game.getDeck().returnCards(cardsReturned);
                return;
            }
        }
        ctx.setActionChallengeLoser(actor);
    }

    public static void executeBlockChallenge(Game game){
        TurnContext ctx = Objects.requireNonNull(game.getTurnContext(), "Context is not initialized");
        ActionType action = ctx.getAction();
        Player blocker = ctx.getBlocker();
        Player blockChallenger = ctx.getBlockChallenger();

        if(blocker.equals(Player.NONE)) throw new IllegalStateException("Blocker not set");
        if(blockChallenger.equals(Player.NONE)) throw new IllegalStateException("Block challenger not set");

        for(Card card: blocker.getPlayingCards()){
            if(card.getType().canBlock(action)){ 
                ctx.setBlockChallengeLoser(blockChallenger);
                List<Card> cardsDrawn = game.getDeck().dealCards(1);
                List<Card> cardsReturned = List.of(card);
                blocker.exchangeCards(cardsDrawn, cardsReturned);
                game.getDeck().returnCards(cardsReturned);
                return;
            }
        }
       ctx.setBlockChallengeLoser(blockChallenger);
    }
    
    public static void executeAction(Game game) {
        TurnContext ctx = Objects.requireNonNull(game.getTurnContext(), "Context is not initialized");
        if(!ctx.canResolveAction()) throw new IllegalStateException("Cannot resolve action");
        ActionType action = ctx.getAction();
        Player actor = ctx.getActor();
        Treasury treasury = game.getTreasury();
        Player target = ctx.getTarget();

        treasury.takeCoins(actor, action.cost);
        if(!ctx.canExecuteAction())return;

        switch (action) {

            case INCOME -> treasury.giveCoins(actor, action.gain);

            case FOREIGN_AID -> treasury.giveCoins(actor, action.gain);

            case TAX -> treasury.giveCoins(actor, action.gain);

            case EXCHANGE -> game.isGameOver(); //game.startExcahnge(target);
            
            case STEAL -> {
                int steal = Math.min(target.getCoins(), action.gain);
                actor.addCoins(steal);
                target.removeCoins(steal);
            }

            case ASSASSINATE -> game.isGameOver(); // game.startReveal(target);
            
            case COUP -> game.isGameOver(); // game.startReveal(target);
        }
    }

}