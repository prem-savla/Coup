package com.game.coup.domain;

import java.util.List;

import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Deck;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;
import com.game.coup.domain.turn.EventFlow;
import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.FlowState;
import com.game.coup.domain.definitions.GamePhase;
import com.game.coup.domain.definitions.GameState;

public class Game {
    private final List<Player> players;
    private final Deck deck;
    private final Treasury treasury;

    private EventFlow event;

    private int currentTurnIndex;

    private GamePhase gamePhase;


    public Game(List<Player> players){
        this.players = List.copyOf(players);

        this.deck = new Deck();
        this.treasury = new Treasury();

        this.currentTurnIndex = 0;

        dealInitialCards();
        distributeInitialCoins();

    }

    private void dealInitialCards() {
        for (Player player : players) {
            player.addCards(deck.dealCards(2));
        }
    }

    private void distributeInitialCoins() {
        for (Player player : players) {
            treasury.giveCoins(player, 2);
        }
    }

//---------getter setters------------------------

    public GameState getGameState() {
        return gamePhase.getGameState();
    }

    public Treasury getTreasury(){ return treasury;}

    public Deck getDeck(){ return deck;}

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

//----------- player & turn ---------------------
    public void nextTurn() {
        if(getAlivePlayers().size() <= 1) gamePhase = GamePhase.GAME_OVER;
        else{
            do {
                currentTurnIndex = (currentTurnIndex + 1) % players.size();
            } while (!players.get(currentTurnIndex).isAlive());
            event = null;
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }


// ------------- utils -----------------------------
    public boolean isGameOver() {
        return gamePhase == GamePhase.GAME_OVER;
    }


}
