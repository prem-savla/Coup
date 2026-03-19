package com.game.coup.domain.turn;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.model.Player;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TurnContext {

    // --- Core (always required) ---
    private final @NonNull Player actor;
    private final @NonNull ActionType action;

    // --- Optional ---
    private Player target;

    // --- Interaction participants ---
    private Player challenger;    
    private Player blocker;     
    private Player blockChallenger;   

    // --- Resolution outcomes ---
    private Player actionChallengeLoser; 
    private Player blockChallengeLoser;

    // --- Constructors  & init ---

    private void init(){
        target = Player.NONE;
        challenger = Player.NONE;
        blocker = Player.NONE;
        blockChallenger = Player.NONE;
        actionChallengeLoser = Player.NONE;
        blockChallengeLoser = Player.NONE;
    }

    private void validateActor(){
        if(actor.equals(Player.NONE))
            throw new IllegalStateException("Actor Null");
    }

    // Without target
    public TurnContext(@NonNull Player actor, @NonNull ActionType action) {
        init();
        this.actor = actor;
        this.action = action;
        validateActor();
    }

    // With target
    public TurnContext(@NonNull Player actor,
                       @NonNull ActionType action,
                       @NonNull Player target) {
        init();
        this.actor = actor;
        this.action = action;
        this.target = target;
        validateActor();
    }

    // --- Validating Setters ---

    public void setTarget(@NonNull Player target){

        if(target.equals(Player.NONE))
            throw new IllegalStateException("Target Null");

        this.target = target;
    }

    public void setChallenger(@NonNull Player challenger){

        if(challenger.equals(Player.NONE))
            throw new IllegalStateException("Challenger Null");

        this.challenger = challenger;
    }

    public void setBlocker(@NonNull Player blocker){

        if(blocker.equals(Player.NONE))
            throw new IllegalStateException("Blocker Null");

        this.blocker = blocker;
    }

    public void setBlockChallenger(@NonNull Player blockChallenger){

        if(blockChallenger.equals(Player.NONE))
            throw new IllegalStateException("Block Challenger Null");

        if(blocker.equals(Player.NONE))
            throw new IllegalStateException("No block to challenge");

        this.blockChallenger = blockChallenger;
    }

    public void setActionChallengeLoser(@NonNull Player loser) {

        if (challenger.equals(Player.NONE))  
            throw new IllegalStateException("No action challenge exists");
        if (!loser.equals(actor) && !loser.equals(challenger))
            throw new IllegalArgumentException("Loser must be either actor or challenger");

        this.actionChallengeLoser = loser;
    }

    public void setBlockChallengeLoser(@NonNull Player loser) {

        if (blockChallenger.equals(Player.NONE)) 
            throw new IllegalStateException("No block challenge exists");
        if (!loser.equals(blocker) && !loser.equals(blockChallenger)) 
            throw new IllegalArgumentException("Loser must be either blocker or block challenger");

        this.blockChallengeLoser = loser;
    }

    // --- utils ---

    public boolean canResolveAction(){
         
        if (!challenger.equals(Player.NONE) && 
            actionChallengeLoser.equals(Player.NONE)) 
            return false;
     
        if (!blocker.equals(Player.NONE) && 
            !blockChallenger.equals(Player.NONE) && 
            blockChallengeLoser.equals(Player.NONE)) 
            return false;
            
        return true;
    }

    public boolean canExecuteAction(){
        if(!canResolveAction()) 
            return false;
        if(actionChallengeLoser.equals(actor)) 
            return false;
        if(!blockChallengeLoser.equals(blocker) && !blocker.equals(Player.NONE)) 
            return false;

        return true;
    }
}