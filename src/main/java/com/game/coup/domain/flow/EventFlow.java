package com.game.coup.domain.flow;

import java.util.*;

import com.game.coup.definitions.ActionType;
import com.game.coup.entities.Card;
import com.game.coup.entities.Player;

interface EventState {
    EventState challenge(EventFlow event, Player challenger);
    EventState block(EventFlow event, Player blocker);
    EventState challengeBlock(EventFlow event, Player challengeBlocker);
    EventState resolve(EventFlow event);
    List<State> getState(EventFlow event);
    
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

        public EventFlow(ActionType action, Player actor, Player target) {

            new SanityChecker(action, actor, target).validate();
            
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
    }
    public List<State> getInitState() { 
        List<State> initState = new ArrayList<>();
        initState.add(State.RESOLVE);
        if(action.challengeable)initState.add(State.CHALLENGE);
        if(action.blockable)initState.add(State.BLOCK);
        return initState;
    }

    public void start(State state) {
        if (state == State.CHALLENGE && action.challengeable) {
            currentState = challengeState;
        } else if (state == State.BLOCK && action.blockable) {
            currentState = blockState;
        } else if (state == State.RESOLVE) {
            currentState = resolveState;
        } else {
            throw new IllegalStateException("Invalid starting state: " + state);
        }
    }

    // Delegate to current state
    public void challenge(Player challenger) { currentState = currentState.challenge(this, challenger); }

    public void block(Player blocker) { currentState = currentState.block(this, blocker); }

    public void challengeBlock(Player challengeBlocker) { currentState = currentState.challengeBlock(this, challengeBlocker); }

    public void resolve() { currentState = currentState.resolve(this); }

    public List<State> getState() { return currentState.getState(this);}

    // getter for states

    public EventState getChallengeState() { return challengeState; }

    public EventState getBlockState() { return blockState; }

    public EventState getChallengeBlockState() { return challengeBlockState; }

    public EventState getResolveState() { return resolveState; }

     public EventState getDeadState() { return deadState; }


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
        event.setChallenger(challenger);;
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
     public List<State> getState(EventFlow event){
        return new ArrayList<State>(List.of(State.CHALLENGE));
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
    public List<State> getState(EventFlow event){
        return new ArrayList<State>(List.of(State.BLOCK, State.RESOLVE));
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
            event.setBlockerWon(false);
        }
        return event.getResolveState();
    }

    @Override
    public List<State> getState(EventFlow event){
        return new ArrayList<State>(List.of(State.CHALLENGE_BLOCK));
    }
}

class ResolveState extends AbstractEventState {

    @Override public EventState resolve(EventFlow event){
        perform(event);
        return event.getDeadState();
    }

    @Override
    public List<State> getState(EventFlow event){
        return new ArrayList<State>(List.of(State.RESOLVE));
    }

    public void perform(EventFlow event){
        new Resolver(event.getAction(), 
        event.getActor(), 
        event.getTarget(),
        event.getChallenger(),
        event.getBlocker(), 
        event.getChallengeBlocker(),
        event.isChallengerWon(),
        event.isBlockerWon()).perform();
    }; 
}

class DeadState extends AbstractEventState{
    @Override
    public List<State> getState(EventFlow event){
        return new ArrayList<State>();
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
