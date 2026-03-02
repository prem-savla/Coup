package com.game.coup.domain;

import java.util.List;

import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Deck;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.FlowState;
import com.game.coup.domain.definitions.GameState;
import com.game.coup.domain.flow.EventFlow;

public class Game {
    private final List<Player> players;
    private final Deck deck;
    private final Treasury treasury;

    private EventFlow event;

    private int currentTurnIndex;

    private GameState gameState;

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

    public Treasury getTreasury(){ return treasury;}

//----------- player & turn ---------------------
    public void nextTurn() {
        if(getAlivePlayers().size() == 1) gameState = GameState.GAME_OVER;
        else{
            gameState = GameState.WAITING_FOR_ACTION;
            do {
                currentTurnIndex = (currentTurnIndex + 1) % players.size();
            } while (!players.get(currentTurnIndex).isAlive());
            event = null;
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }

// ------------- actions & counters ------------------
    public void performAction(ActionType action ){
        if(action == null)  throw new IllegalArgumentException("Action null");
        if(gameState != GameState.WAITING_FOR_ACTION)  throw new IllegalStateException("Action already in progress");

        event = new EventFlow(action, getCurrentPlayer(), Player.NONE,this);
    }

    public void performTargetedAction(ActionType action, String targetName ){
        if (action == null) throw new IllegalArgumentException("Action cannot be null");
        if (targetName == null) throw new IllegalArgumentException("Target required");
        if(gameState != GameState.WAITING_FOR_ACTION)  throw new IllegalStateException("Action already in progress");

        Player target = getPlayerByName(targetName);
        event = new EventFlow(action, getCurrentPlayer(), target,this);
    }

    public void performCounters(FlowState state, String playerName) {
        if (state == null) throw new IllegalArgumentException("Flow state cannot be null");
        if (playerName == null) throw new IllegalArgumentException("Player required");
        if(event == null || gameState!=GameState.IN_FLOW) throw new IllegalStateException("Need to start an action first");

        Player p = getPlayerByName(playerName);
        event.performAction(state,p);
    }

    public void performResolve(FlowState state) {
        if (state == null) throw new IllegalArgumentException("Flow state cannot be null");
        if(event == null || gameState!=GameState.IN_FLOW) throw new IllegalStateException("Need to start an action first");

        event.performAction(state,Player.NONE);
        nextTurn();
    }

    public List<FlowState> getOptions(){
        if(event == null) throw new IllegalStateException("Need to start an action first");
        return event.getState();
    }
// --------------exchange and reveal---------------
    public List<Card> startExchange(){
        if(gameState!= GameState.IN_FLOW) throw new IllegalStateException("Start an action to exchange");
        gameState = GameState.WAITING_FOR_EXCHANGE;
        List<Card> opt = deck.dealTwoCards();
        return opt;
    }

    public void endExchange(List<Card> cardsTaken, List<Card> cardsNotTaken){
        if(gameState!= GameState.WAITING_FOR_EXCHANGE) throw new IllegalStateException("Wating for exchange");
        getCurrentPlayer().exchangeCards(cardsTaken);
        deck.returnTwoCards(cardsNotTaken);
        gameState = GameState.WAITING_FOR_ACTION;
    }

    public Player startReveal(Player player){
        if(gameState!= GameState.IN_FLOW) throw new IllegalStateException("Start an action to reveal");
        gameState = GameState.WAITING_FOR_REVEAL;
        return player;
    }

    public void endReveal(Player player, Card card){
        if(gameState!= GameState.WAITING_FOR_EXCHANGE) throw new IllegalStateException("Waiting to reveal");
        player.revealCard(card);
        gameState = GameState.WAITING_FOR_ACTION;

    }

// ------------- utils -----------------------------
    public boolean isGameOver(){
        return gameState == GameState.GAME_OVER? true: false;
    }

    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException("Player not found: " + name));
    }

    public List<Player> getAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .toList();
    }
}
