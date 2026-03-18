package com.game.coup.domain.flow;

import com.game.coup.domain.Game;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;

public class Resolver {
    private ActionType action;
    private Player actor;
    private Player target;
    private Player challenger;
    private Player blocker;
    private Player challengeBlocker;
    
    private boolean challengerWon;
    private boolean blockerWon;

    private Treasury treasury;
    private Game game;

    public Resolver(ActionType action, 
        Player actor, 
        Player target,
        Player challenger,
        Player blocker,
        Player challengeBlocker,
        boolean challengerWon,
        boolean blockerWon,
        Game Game
    ) {
        this.action = action;
        this.actor = actor;
        this.target = target;
        this.challenger = challenger;
        this.blocker = blocker;
        this.challengeBlocker = challengeBlocker;
        this.challengerWon = challengerWon;
        this.blockerWon = blockerWon;
        
        this.treasury  = game.getTreasury();
    }

    public void perform(){

        if(!challenger.equals(Player.NONE) ){
            if( challengerWon){
                //game.startReveal();(actor);
                treasury.takeCoins(actor, action.cost);
                return;
            }else{
                //game.startReveal();(challenger);
            }
        }

        if(!blocker.equals(Player.NONE)) {
            if(blockerWon){
                if(!challengeBlocker.equals(Player.NONE)){
                    //game.startReveal();(challengeBlocker);
                    treasury.takeCoins(actor, action.cost);
                }
            }else{
                //game.startReveal();(blocker);
                doAction(action, actor, target);
            }
            
        }else {
            doAction(action, actor, target);
        }
    }

    public void doAction(ActionType actionType, Player actor, Player target){
        switch (actionType) {
            case ActionType.INCOME:
                treasury.giveCoins(actor , ActionType.INCOME.gain);
                break;
            case ActionType.FOREIGN_AID:
                treasury.giveCoins(actor , ActionType.FOREIGN_AID.gain);
                break;
            case ActionType.TAX:
                treasury.giveCoins(actor , ActionType.TAX.gain);
                break;
            case ActionType.EXCHANGE:
                // game.startExchange();
                break;
            case ActionType.STEAL:
                int steal = Math.min(target.getCoins(), ActionType.STEAL.gain);
                actor.addCoins(steal);
                target.removeCoins(steal);
                break;
            case ActionType.ASSASSINATE:
                //game.startReveal();(target);
                treasury.takeCoins(actor, ActionType.ASSASSINATE.cost);
                break;
            case ActionType.COUP:
                //game.startReveal();(target);
                treasury.takeCoins(actor, ActionType.COUP.cost);
                break;
            default:
                break;
        }
    }

}
