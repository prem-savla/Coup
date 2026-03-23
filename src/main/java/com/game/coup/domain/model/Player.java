package com.game.coup.domain.model;

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
        this.coins = 0;
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

    // --- Null Player ---

    public static final Player NONE = new Player();

    private Player() {
        this.name = "__NONE__";
        this.playingCards = Collections.emptyList();
        this.revealedCards = Collections.emptyList();
        this.coins = 0;
        this.alive = false;
    }

    // ---  Coin Handling ---
  
    public void addCoins(int amount) {
        ensureAlive();

        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        coins += amount;
    }

    public void removeCoins(int amount) {
        ensureAlive();

        if (amount < 0) {
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

    // --- Card Handling ---

    public void addCards(List<Card> cards) {
        ensureAlive();

        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("Cards cannot be null or empty.");
        }
        
        if(playingCards.size()+cards.size()>2){
            throw new IllegalArgumentException("Cards cannot be more than 2");
        }

        playingCards.addAll(cards);
    }

    public void exchangeCards(List<Card> cardsDrawn, List<Card> cardsReturned){
        ensureAlive();
        if (cardsDrawn == null || cardsDrawn.isEmpty()) throw new IllegalArgumentException("Cards taken cannot be null or empty.");
        if (cardsReturned == null || cardsReturned.isEmpty()) throw new IllegalArgumentException("Cards released cannot be null or empty.");
        if (cardsDrawn.size() != cardsReturned.size()  ) throw new IllegalArgumentException("Cards taken and released should be same");

        int originalCount = playingCards.size();
        
        for (Card card : cardsReturned) {
            boolean removed = playingCards.remove(card);
            if (!removed) throw new IllegalStateException("Cannot release card not owned by player: " + card.getId());
        }
        
        for (Card card : cardsDrawn) {
            if (playingCards.contains(card) || !cardsReturned.contains(card)) throw new IllegalStateException("Cannot take a card already owned and not released: ");
            playingCards.add(card);  
        }
        
        if(playingCards.size()!=originalCount)throw new IllegalArgumentException("Exchanging cannot add or remove cards");
    }

    public void revealCard(Card card) {
        ensureAlive();
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }

        if (!playingCards.contains(card)) {
            throw new IllegalArgumentException("Card not owned by player.");
        }

        boolean removed = playingCards.remove(card);
        if (!removed) throw new IllegalStateException("Cannot release card not owned by player: " + card.getId());
        
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