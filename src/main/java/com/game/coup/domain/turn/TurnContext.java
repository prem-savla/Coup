package com.game.coup.domain.turn;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.model.Player;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TurnContext {

    // --- Core (always required) ---
    private Player actor;
    private ActionType action;

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
        actor = Player.NONE;
        action = null;
        target = Player.NONE;
        challenger = Player.NONE;
        blocker = Player.NONE;
        blockChallenger = Player.NONE;
        actionChallengeLoser = Player.NONE;
        blockChallengeLoser = Player.NONE;
    }

    // Without target
    public TurnContext(@NonNull Player actor, 
                       @NonNull ActionType action) {
        init();
        this.actor = setPlayer(actor, this.actor);
        this.action = action;
    }

    // With target
    public TurnContext(@NonNull Player actor,
                       @NonNull ActionType action,
                       @NonNull Player target) {
        init();
        this.actor = setPlayer(actor, this.actor);
        this.action = action;
        this.target = setPlayer(target, this.target);
    }

    // --- Validating Setters ---

    private Player setPlayer(@NonNull Player newPlayer, @NonNull Player currentPlayer) {

        if (newPlayer.equals(Player.NONE))
            throw new IllegalStateException("Invalid player");

        if (!currentPlayer.equals(Player.NONE))
            throw new IllegalStateException("Player already set");

        return newPlayer;
    }

    public void setChallenger(@NonNull Player challenger) {this.challenger = setPlayer(challenger, this.challenger);}

    public void setBlocker(@NonNull Player blocker) {this.blocker = setPlayer(blocker, this.blocker);}

    public void setBlockChallenger(@NonNull Player blockChallenger) {this.blockChallenger = setPlayer(blockChallenger, this.blockChallenger);}

    public void setActionChallengeLoser(@NonNull Player loser) {this.actionChallengeLoser = setPlayer(loser, this.actionChallengeLoser);}

    public void setBlockChallengeLoser(@NonNull Player loser) {this.blockChallengeLoser = setPlayer(loser, this.blockChallengeLoser);}

    // --- resolution ---

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