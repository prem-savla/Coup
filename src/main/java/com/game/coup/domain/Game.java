package com.game.coup.domain;

import java.util.List;

import com.game.coup.domain.model.Deck;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.FlowState;
import com.game.coup.domain.flow.EventFlow;

public class Game {
    private final List<Player> players;
    private final Deck deck;
    private final Treasury treasury;

    private EventFlow event;

    private int currentTurnIndex;

    public Game(List<Player> players){
        this.players = List.copyOf(players);

        this.deck = new Deck();
        this.treasury = new Treasury();

        this.currentTurnIndex = 0;

        dealInitialCards();
    }

    private void dealInitialCards() {
        for (Player player : players) {
            player.addCards(deck.dealTwoCards());
        }
    }

//----------- player & turn mgm ---------------------
    public void nextTurn() {
        do {
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
        } while (!players.get(currentTurnIndex).isAlive());
        event = null;
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }

    public List<Player> getAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .toList();
    }

// ------------- actions & counters ------------------
    public void performAction(ActionType action ){
        event = new EventFlow(action, getCurrentPlayer(), Player.NONE,treasury, deck);
    }

    public void performTargetedAction(ActionType action, String targetName ){
        Player target = getPlayerByName(targetName);
        event = new EventFlow(action, getCurrentPlayer(), target,treasury, deck);
    }

    public void performCounters(FlowState state, String pName) {
        if(event == null) throw new IllegalStateException("Need to start an action first");
        Player p = getPlayerByName(pName);
        event.performAction(state,p);
    }

    public void performResolve(FlowState state) {
        if(event == null) throw new IllegalStateException("Need to start an action first");
        event.performAction(state,Player.NONE);
    }

    public List<FlowState> getOptions(){
        if(event == null) throw new IllegalStateException("Need to start an action first");
        return event.getState();
    }

// ------------- utils -----------------------------
    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException("Player not found: " + name));
    }
}

//turn state