package com.game.coup.domain.turn;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.model.Player;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
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
    private boolean blocked;
    
    private Player actionChallengeLoser;   
    private Player blockChallengeLoser;

    // --- Constructors  & init ---

    private void init(){
        challenger = Player.NONE;
        blocker = Player.NONE;
        blockChallenger = Player.NONE;
        actionChallengeLoser = Player.NONE;
        blockChallengeLoser = Player.NONE;
        blocked = false;
    }

    // Without target
    public TurnContext(@NonNull Player actor, @NonNull ActionType action) {
        init();
        this.actor = actor;
        this.action = action;
    }

    // With target
    public TurnContext(@NonNull Player actor,
                       @NonNull ActionType action,
                       @NonNull Player target) {
        init();
        this.actor = actor;
        this.action = action;
        this.target = target;
    }
}