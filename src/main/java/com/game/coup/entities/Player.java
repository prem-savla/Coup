package com.game.coup.entities;

import java.util.*;

public class Player {

    private final String name;
    private final List<Card> playingCards;
    private final List<Card> revealedCards;
    private int coins;
    private boolean alive;

    

    public Player(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }

        this.name = name;
        this.playingCards = new ArrayList<>();
        this.revealedCards = new ArrayList<>();
        this.coins = 2;
        this.alive = true;
    }

    private void ensureAlive() {
        if (!alive) {
            throw new IllegalStateException("Player is eliminated.");
        }
    }

    public boolean isAlive(){
        return alive;
    }

    public String getName() {
        return name;
    }

    // --------------------------
    // Null Player
    // --------------------------

    public static final Player NONE = new Player();

    private Player() {
        this.name = "__NONE__";
        this.playingCards = Collections.emptyList();
        this.revealedCards = Collections.emptyList();
        this.coins = 0;
        this.alive = false;
    }

    public boolean isNone() {
        return this == NONE;
    }

    // --------------------------
    // Coin Handling
    // --------------------------

    public void addCoins(int amount) {
        ensureAlive();

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        coins += amount;
    }

    public void removeCoins(int amount) {
        ensureAlive();

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        if (coins < amount) {
            throw new IllegalStateException("Not enough coins.");
        }

        coins -= amount;
    }

    public int getCoins() {
        return coins;
    }

    // --------------------------
    // Card Handling
    // --------------------------

    public void addCard(Card card) {
        ensureAlive();

        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }

        playingCards.add(card);
    }

    public void revealCard(Card card) {
        ensureAlive();

        if (!playingCards.contains(card)) {
            throw new IllegalArgumentException("Card not owned by player.");
        }

        playingCards.remove(card);
        revealedCards.add(card);

        if (playingCards.isEmpty()) {
            alive = false;
        }
    }

    public List<Card> getPlayingCards() {
        return Collections.unmodifiableList(playingCards);
    }

    public List<Card> getRevealedCards() {
        return Collections.unmodifiableList(revealedCards);
    }

}