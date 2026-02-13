package com.game.coup.domain.flow;

import com.game.coup.definitions.ActionType;
import com.game.coup.entities.Player;
import com.game.coup.entities.Treasury;

public class Resolver {
    private ActionType action;
    private Player actor;
    private Player target;
    private Player challenger;
    private Player blocker;
    private Player challengeBlocker;
    
    private boolean challengerWon;
    private boolean blockerWon;

    Treasury treasury;

    public Resolver(ActionType action, 
        Player actor, 
        Player target,
        Player challenger,
        Player blocker,
        Player challengeBlocker,
        boolean challengerWon,
        boolean blockerWon
    ) {
        this.action = action;
        this.actor = actor;
        this.target = target;
        this.challenger = challenger;
        this.blocker = blocker;
        this.challengeBlocker = challengeBlocker;
        this.challengerWon = challengerWon;
        this.blockerWon = blockerWon;
        
        treasury  = null;
    }

    public void perform(){

        if(!challenger.equals(Player.NONE) ){
            if( challengerWon){
                // Func.forceReveal(actor);
                treasury.takeCoins(actor, action.cost);
                return;
            }else{
                // Func.forceReveal(challenger);
            }
        }

        if(!blocker.equals(Player.NONE)) {
            if(blockerWon){
                if(!challengeBlocker.equals(Player.NONE)){
                    // Func.forceReveal(challengeBlocker);
                    treasury.takeCoins(actor, action.cost);
                }
            }else{
                // Func.forceReveal(blocker);
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
                // Func.exchangeAction(actor);
                
                break;
            case ActionType.STEAL:
                int steal = Math.min(target.getCoins(), ActionType.STEAL.gain);
                actor.addCoins(steal);
                target.removeCoins(steal);
                break;
            case ActionType.ASSASSINATE:
                // Func.forceReveal(target);
                treasury.takeCoins(actor, ActionType.ASSASSINATE.cost);
                break;
            case ActionType.COUP:
                // Func.forceReveal(target);
                 treasury.takeCoins(actor, ActionType.COUP.cost);
                break;
            default:
                break;
        }
    }

}
