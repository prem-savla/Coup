package com.game.coup.domain.flow;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.FlowState;
import com.game.coup.domain.model.Player;

import java.util.Objects;

public final class FlowChecker {

    private FlowChecker() {
        throw new AssertionError("Utility class");
    }

    public static void initValidate(
            ActionType action,
            Player actor,
            Player target
    ) {
        Objects.requireNonNull(action, "Action required");
        Objects.requireNonNull(actor, "Actor required");

        if (actor.isNone())
            throw new IllegalArgumentException("Actor required");

        if (!actor.isAlive())
            throw new IllegalStateException("Dead player cannot act");

        if (action.targeted && (target == null || target.isNone()))
            throw new IllegalArgumentException(action + " requires a target");

        if (!action.targeted && target != null && !target.isNone())
            throw new IllegalArgumentException(action + " does not require a target");

        if (action.targeted && target != null && target.equals(actor))
            throw new IllegalArgumentException("Actor cannot target self");

        if (actor.getCoins() >= 10 && action != ActionType.COUP)
            throw new IllegalStateException("Player must coup when holding 10 or more coins");

        if (actor.getCoins() < action.cost)
            throw new IllegalStateException("Not enough coins for action " + action);
    }

    public static void flowValidate(
            EventFlow event,
            FlowState state,
            Player player
    ) {
        Objects.requireNonNull(event, "EventFlow required");
        Objects.requireNonNull(state, "FlowState required");

        if (!event.getState().contains(state))
            throw new IllegalArgumentException("Invalid state transition: " + state);

        if (state == FlowState.RESOLVE) {
            if (player != null && !player.isNone())
                throw new IllegalArgumentException("Resolve does not require a player");
            return;
        }

        if (player == null || player.isNone())
            throw new IllegalArgumentException("Player required for " + state);

        if (!player.isAlive())
            throw new IllegalStateException("Dead player cannot act");

        switch (state) {

            case CHALLENGE -> {
                if (!event.getAction().challengeable)
                    throw new IllegalStateException("Action cannot be challenged");

                if (player.equals(event.getActor()))
                    throw new IllegalStateException("Actor cannot challenge own action");
            }

            case BLOCK -> {
                if (!event.getAction().blockable)
                    throw new IllegalStateException("Action cannot be blocked");

                if (event.getTarget() == null || event.getTarget().isNone())
                    throw new IllegalStateException("No target to block");

                if (!player.equals(event.getTarget()))
                    throw new IllegalStateException("Only target can block");
            }

            case CHALLENGE_BLOCK -> {
                if (event.getBlocker() == null || event.getBlocker().isNone())
                    throw new IllegalStateException("No block to challenge");

                if (player.equals(event.getBlocker()))
                    throw new IllegalStateException("Blocker cannot challenge own block");
            }

            default -> throw new IllegalStateException("Unhandled state: " + state);
        }
    }
}