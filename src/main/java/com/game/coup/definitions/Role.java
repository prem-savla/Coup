package com.game.coup.definitions;

import java.util.Set;

public enum Role {

    DUKE(
        Set.of(ActionType.TAX),
        Set.of(ActionType.FOREIGN_AID)
    ),

    ASSASSIN(
        Set.of(ActionType.ASSASSINATE),
        Set.of()
    ),

    CAPTAIN(
        Set.of(ActionType.STEAL),
        Set.of(ActionType.STEAL)
    ),

    AMBASSADOR(
        Set.of(ActionType.EXCHANGE),
        Set.of(ActionType.STEAL)
    ),

    CONTESSA(
        Set.of(),
        Set.of(ActionType.ASSASSINATE)
    );

    private static final Set<ActionType> GENERAL_ACTIONS = Set.of(
        ActionType.INCOME,
        ActionType.FOREIGN_AID,
        ActionType.COUP
    );

    private final Set<ActionType> actions;
    private final Set<ActionType> counterActions;

    Role(Set<ActionType> actions, Set<ActionType> counterActions) {
        this.actions = actions;
        this.counterActions = counterActions;
    }

    public boolean canPerform(ActionType action) {
        return GENERAL_ACTIONS.contains(action) || actions.contains(action);
    }

    public boolean canBlock(ActionType action) {
        return counterActions.contains(action);
    }
}