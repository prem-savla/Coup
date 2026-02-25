package com.game.coup.domain.flow;

import java.util.*;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.FlowState;
import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;

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

    private EventState challengeState;
    private EventState blockState;
    private EventState challengeBlockState;
    private EventState resolveState;
    private EventState deadState;

    protected Treasury treasury;

    public EventFlow(ActionType action, Player actor, Player target, Treasury treasury) {

        new SanityChecker(action, actor, target, treasury).validate();
        
        this.action = action;
        this.actor = actor;
        this.target = target;
        this.blocker = Player.NONE;
        this.challengeBlocker = Player.NONE;

        this.challengeState = new ChallengeState();
        this.blockState = new BlockState();
        this.challengeBlockState = new ChallengeBlockState();
        this.resolveState = new ResolveState();
        this.deadState = new DeadState();

        currentState = getDeadState();
        // defaults for boolean 
        challengerWon = false;
        blockerWon = false;

        this.treasury = treasury;
    }

    public List<FlowState> getInitState() { 
        List<FlowState> initState = new ArrayList<>();
        initState.add(FlowState.RESOLVE);
        if(action.challengeable)initState.add(FlowState.CHALLENGE);
        if(action.blockable)initState.add(FlowState.BLOCK);
        return initState;
    }

    private void start(FlowState flowState) {
        if (flowState == FlowState.CHALLENGE && action.challengeable) {
            currentState = challengeState;
        } else if (flowState == FlowState.BLOCK && action.blockable) {
            currentState = blockState;
        } else if (flowState == FlowState.RESOLVE) {
            currentState = resolveState;
        } else {
            throw new IllegalStateException("Invalid starting flowState: " + flowState);
        }
    }

    // FSM entry
    public void perform(FlowState flowState, Player player){
        if(currentState.equals(deadState)) start(flowState);

        if(flowState == FlowState.RESOLVE && !player.isNone()) 
            throw new IllegalArgumentException("No player required for resolve");

        switch (flowState) {

            case CHALLENGE -> challenge(player);

            case BLOCK -> block(player);

            case CHALLENGE_BLOCK -> challengeBlock(player);

            case RESOLVE -> resolve();

            default -> throw new IllegalStateException(
                    "Unsupported flowState transition: " + flowState
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
    public Player getActor() { return actor; }
    public void setActor(Player actor) { this.actor = actor; }

    public ActionType getAction() { return action; }
    public void setAction(ActionType action) { this.action = action; }

    public Player getTarget() { return target; }
    public void setTarget(Player target) { this.target = target; }

    public Player getChallenger() { return challenger; }
    public void setChallenger(Player challenger) { this.challenger = challenger; }

    public boolean isChallengerWon() { return challengerWon; }
    public void setChallengerWon(boolean challengerWon) { this.challengerWon = challengerWon; }

    public Player getBlocker() { return blocker; }
    public void setBlocker(Player blocker) { this.blocker = blocker; }

    public boolean isBlockerWon() { return blockerWon; }
    public void setBlockerWon(boolean blockerWon) { this.blockerWon = blockerWon; }

    public Player getChallengeBlocker() { return challengeBlocker; }
    public void setChallengeBlocker(Player challengeBlocker) { this.challengeBlocker = challengeBlocker; }

}

class ChallengeState extends AbstractEventState {

    @Override
    public EventState challenge(EventFlow event, Player challenger) {
        if(challenger.isNone()) throw new IllegalArgumentException("requires challenger");
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
        if(blocker.isNone()) throw new IllegalArgumentException("requires Blocker");
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
        return new ArrayList<FlowState>(List.of(FlowState.BLOCK, FlowState.RESOLVE));
    }
}

class ChallengeBlockState extends AbstractEventState {

    @Override
    public EventState challengeBlock(EventFlow event, Player challengeBlocker) {
        if(challengeBlocker.isNone()) throw new IllegalArgumentException("requires challengeBlocker");
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
        event.treasury )
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
