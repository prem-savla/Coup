package com.game.coup.domain.flow;

import java.util.*;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Deck;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;
import com.game.coup.domain.definitions.FlowState;

interface EventState {
    EventState challenge(EventFlow event, Player challenger);
    EventState block(EventFlow event, Player blocker);
    EventState challengeBlock(EventFlow event, Player challengeBlocker);
    EventState resolve(EventFlow event);
    List<FlowState> getState(EventFlow event);
}


public class EventFlow {
    private Player actor;
    private ActionType action;
    private Player target;
    private Player challenger;
    private Player blocker;
    private Player challengeBlocker;
    
    private boolean challengerWon;
    private boolean blockerWon;

    private EventState currentState;

    private EventState initState;
    private EventState challengeState;
    private EventState blockState;
    private EventState challengeBlockState;
    private EventState resolveState;
    private EventState deadState;

    private Treasury treasury;
    private Deck deck;

    public EventFlow(ActionType action, Player actor, Player target, Treasury treasury, Deck deck) {

        SanityChecker.initValidate(action, actor, target, treasury);
        
        this.action = action;
        this.actor = actor;
        this.target = target;
        this.challenger = Player.NONE;
        this.blocker = Player.NONE;
        this.challengeBlocker = Player.NONE;
        
        this.treasury = treasury;
        this.deck = deck;

        // defaults for boolean 
        challengerWon = false;
        blockerWon = false;

        this.initState = new initState();
        this.challengeState = new ChallengeState();
        this.blockState = new BlockState();
        this.challengeBlockState = new ChallengeBlockState();
        this.resolveState = new ResolveState();
        this.deadState = new DeadState();

        currentState = initState;
    }

    // FSM entry
    public void performAction(FlowState flowState, Player player){
        SanityChecker.flowValidate(this, flowState, player);

        switch (flowState) {

            case CHALLENGE -> challenge(player);

            case BLOCK -> block(player);

            case CHALLENGE_BLOCK -> challengeBlock(player);

            case RESOLVE -> resolve();

            default -> throw new IllegalStateException(
                    "Unsupported state transition: " + flowState
            );
        }
    }

    // Delegate to current flowState
    private void challenge(Player challenger) { currentState = currentState.challenge(this, challenger); }

    private void block(Player blocker) { currentState = currentState.block(this, blocker); }

    private void challengeBlock(Player challengeBlocker) { currentState = currentState.challengeBlock(this, challengeBlocker); }

    private void resolve() { currentState = currentState.resolve(this); }

    public List<FlowState> getState() { return currentState.getState(this);}

    // getter for states

    protected EventState getChallengeState() { return challengeState; }

    protected EventState getBlockState() { return blockState; }

    protected EventState getChallengeBlockState() { return challengeBlockState; }

    protected EventState getResolveState() { return resolveState; }

    protected EventState getDeadState() { return deadState; }


    // getter setter for variables
    protected Player getActor() { return actor; }
    protected void setActor(Player actor) { this.actor = actor; }

    protected ActionType getAction() { return action; }
    protected void setAction(ActionType action) { this.action = action; }

    protected Player getTarget() { return target; }
    protected void setTarget(Player target) { this.target = target; }

    protected Player getChallenger() { return challenger; }
    protected void setChallenger(Player challenger) { this.challenger = challenger; }

    protected boolean isChallengerWon() { return challengerWon; }
    protected void setChallengerWon(boolean challengerWon) { this.challengerWon = challengerWon; }

    protected Player getBlocker() { return blocker; }
    protected void setBlocker(Player blocker) { this.blocker = blocker; }

    protected boolean isBlockerWon() { return blockerWon; }
    protected void setBlockerWon(boolean blockerWon) { this.blockerWon = blockerWon; }

    protected Player getChallengeBlocker() { return challengeBlocker; }
    protected void setChallengeBlocker(Player challengeBlocker) { this.challengeBlocker = challengeBlocker; }

    protected Treasury getTreasury(){ return treasury; }
    protected Deck getDeck(){ return deck; }

}

class initState extends AbstractEventState{

    @Override
    public EventState challenge(EventFlow event, Player challenger) {
        return event.getChallengeState().challenge(event, challenger);
    }

    @Override
    public EventState block(EventFlow event, Player blocker) {
        return event.getBlockState().block(event, blocker);
    }

    @Override
    public EventState resolve(EventFlow event) {
        return event.getResolveState().resolve(event);
    }

    @Override
    public List<FlowState> getState(EventFlow event) { 
        List<FlowState> state = new ArrayList<>();
        state.add(FlowState.RESOLVE);
        if(event.getAction().challengeable)state.add(FlowState.CHALLENGE);
        if(event.getAction().blockable)state.add(FlowState.BLOCK);
        return state;
    }
}

class ChallengeState extends AbstractEventState {

    @Override
    public EventState challenge(EventFlow event, Player challenger) {
        event.setChallenger(challenger);
        for(Card card :event.getActor().getPlayingCards() ){
            if(card.getType().canPerform(event.getAction())){
                event.setChallengerWon(false);
                return event.getBlockState();
            }
        }
        event.setChallengerWon(true);
        return event.getResolveState();
        
    }

    @Override
     public List<FlowState> getState(EventFlow event){
        return new ArrayList<FlowState>(List.of(FlowState.CHALLENGE));
    }
}

class BlockState extends AbstractEventState {

    @Override
    public EventState block(EventFlow event, Player blocker) {
        event.setBlocker(blocker);
        event.setBlockerWon(true);
        return event.getChallengeBlockState();
    }

    @Override
    public EventState resolve(EventFlow event) {
        return event.getResolveState();
    }

    @Override
    public List<FlowState> getState(EventFlow event){
        List<FlowState> state = new ArrayList<>();
        state.add(FlowState.RESOLVE);
        if(event.getAction().blockable)state.add(FlowState.BLOCK);
        return state;
    }
}

class ChallengeBlockState extends AbstractEventState {

    @Override
    public EventState challengeBlock(EventFlow event, Player challengeBlocker) {
        event.setChallengeBlocker(challengeBlocker);

        for(Card card : event.getBlocker().getPlayingCards()){
            if(card.getType().canBlock(event.getAction())){
                event.setBlockerWon(true);
                return event.getResolveState();
            }
        }
        event.setBlockerWon(false);
        return event.getResolveState();
    }

    @Override
    public List<FlowState> getState(EventFlow event){
        return new ArrayList<FlowState>(List.of(FlowState.CHALLENGE_BLOCK));
    }
}

class ResolveState extends AbstractEventState {

    @Override public EventState resolve(EventFlow event){
        perform(event);
        return event.getDeadState();
    }

    @Override
    public List<FlowState> getState(EventFlow event){
        return new ArrayList<FlowState>(List.of(FlowState.RESOLVE));
    }

    public void perform(EventFlow event){
        new Resolver(event.getAction(), 
        event.getActor(), 
        event.getTarget(),
        event.getChallenger(),
        event.getBlocker(), 
        event.getChallengeBlocker(),
        event.isChallengerWon(),
        event.isBlockerWon(),
        event.getTreasury(),
        event.getDeck())
        .perform();
    }; 
}

class DeadState extends AbstractEventState{
    @Override
    public List<FlowState> getState(EventFlow event){
        return new ArrayList<FlowState>();
    }
}


abstract class AbstractEventState implements EventState {

    protected RuntimeException illegal() {
        String method = Thread.currentThread()
                .getStackTrace()[2]
                .getMethodName();

        return new IllegalStateException(method);
    }

    @Override public EventState challenge(EventFlow e, Player p) { throw illegal(); }
    @Override public EventState block(EventFlow e, Player p) { throw illegal(); }
    @Override public EventState challengeBlock(EventFlow e, Player p) { throw illegal(); }
    @Override public EventState resolve(EventFlow e) { throw illegal(); }
}
