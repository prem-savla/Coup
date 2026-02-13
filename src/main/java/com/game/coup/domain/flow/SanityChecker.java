package com.game.coup.domain.flow;

import com.game.coup.definitions.ActionType;
import com.game.coup.entities.Player;

public class SanityChecker {

    private final Player actor;
    private final ActionType action;
    private final Player target;

    public SanityChecker(
            ActionType action,
            Player actor,
            Player target
    ) {
        this.action = action;
        this.actor = actor;
        this.target = target;
    }

    public void validate() {

        if (action.targeted && target == Player.NONE) {
            throw new IllegalArgumentException(
                action + " requires a target"
            );
        }

        if (!action.targeted && target != Player.NONE) {
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