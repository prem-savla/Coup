package com.game.coup.definitions;

public enum ActionType {
    INCOME(false, false,false,0,1),
    FOREIGN_AID(true, false,false,0,2),
    STEAL(true, true,true,0,2),
    TAX(false, true,false,0,3),
    ASSASSINATE(true, true,true,3,0) ,
    EXCHANGE(false, true,false,0,0),
    COUP(false, false,true,7,0);

    public final boolean blockable;
    public final boolean challengeable;
    public final boolean targeted;
    public final int cost;
    public final int gain;

    ActionType(boolean blockable, boolean challengeable, boolean targeted, int cost, int gain) {
        this.blockable = blockable;
        this.challengeable = challengeable;
        this.targeted = targeted;
        this.cost = cost;
        this.gain = gain;
    }

}