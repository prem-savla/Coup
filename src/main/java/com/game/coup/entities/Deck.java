package com.game.coup.entities;

import com.game.coup.definitions.Role;

import java.util.*;

public class Deck {

    private static final int EXCHANGE_COUNT = 2;

    private final List<Card> cards = new ArrayList<>();
    private final Set<String> originalCardIds = new HashSet<>();

    public Deck() {
        initialize();
    }

    private void initialize() {
        for (Role role : Role.values()) {
            for (int i = 0; i < 3; i++) {
                Card card = new Card(role);
                cards.add(card);
                originalCardIds.add(card.getId());
            }
        }

        shuffle();
    }

    public List<Card> dealTwoCards() {
        List<Card> dealt = new ArrayList<>(EXCHANGE_COUNT);
        for (int i = 0; i < EXCHANGE_COUNT; i++) {
            dealt.add(cards.remove(cards.size() - 1));
        }
        return dealt;
    }

    public void returnTwoCards(List<Card> returned) {
        if (returned == null || returned.size() != EXCHANGE_COUNT) {
            throw new IllegalArgumentException("Exactly two cards must be returned.");
        }
        for (Card card : returned) {
            if (!originalCardIds.contains(card.getId())) {
                throw new IllegalStateException("Card does not belong to this deck.");
            }
            if (cards.contains(card)) {
                throw new IllegalStateException("Duplicate card returned.");
            }
        }

        cards.addAll(returned);
        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(cards);
    }
}