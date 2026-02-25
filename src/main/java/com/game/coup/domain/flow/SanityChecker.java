package com.game.coup.domain.flow;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;

public class SanityChecker {

    private final Player actor;
    private final ActionType action;
    private final Player target;
    private final Treasury treasury;

    public SanityChecker(
            ActionType action,
            Player actor,
            Player target,
            Treasury treasury
    ) {
        this.action = action;
        this.actor = actor;
        this.target = target;
        this.treasury = treasury;
    }

    public void validate() {

        if(treasury == null){
            throw new IllegalArgumentException(
                "treasury not found"
            );
        }

        if (actor.isNone()) {
            throw new IllegalArgumentException(
                " requires a actor"
            );
        }

        if (action.targeted && target.isNone()) {
            throw new IllegalArgumentException(
                action + " requires a target"
            );
        }

        if (!action.targeted && !target.isNone()) {
            throw new IllegalArgumentException(
                action + " does not require a target"
            );
        }

        if (actor.getCoins() >= 10 && action != ActionType.COUP) {
            throw new IllegalStateException(
                "Player must coup when holding 10 or more coins"
            );
        }

        if (actor.getCoins() < action.cost) {
            throw new IllegalStateException(
                "Not enough coins for action " + action
            );
        }

    }

}