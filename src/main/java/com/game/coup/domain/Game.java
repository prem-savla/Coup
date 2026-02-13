package com.game.coup.domain;

import java.util.List;
import java.util.UUID;

import com.game.coup.entities.Deck;
import com.game.coup.entities.Player;
import com.game.coup.entities.Treasury;

public class Game {

    private final String roomId;

    private final List<Player> players;
    private final Deck deck;
    private final Treasury treasury;

    private int currentTurnIndex;

    public Game(List<Player> players) {
        this.roomId = UUID.randomUUID().toString();
        this.players = players;

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

    public String getRoomId() {
        return roomId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }

    public void nextTurn() {
        currentTurnIndex =
                (currentTurnIndex + 1) % players.size();
    }

    public Treasury getTreasury() {
        return treasury;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Player> getAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .toList();
    }

    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException("Player not found: " + name));
    }
}