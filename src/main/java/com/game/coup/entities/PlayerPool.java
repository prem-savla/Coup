package com.game.coup.entities;

import java.util.*;

public class PlayerPool {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;

    private final Map<String, Player> players = new LinkedHashMap<>();

    public void addPlayer(Player player) {

        if (player == null || player.isNone()) {
            throw new IllegalArgumentException("Invalid player.");
        }

        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("Maximum players reached.");
        }

        if (players.containsKey(player.getName())) {
            throw new IllegalStateException("Player already exists.");
        }

        players.put(player.getName(), player);
    }

    public Player getPlayer(String name) {
        return players.getOrDefault(name, Player.NONE);
    }

    public List<Player> getAll() {
        return Collections.unmodifiableList(
                new ArrayList<>(players.values())
        );
    }

    public List<Player> getAlivePlayers() {
        return players.values()
                .stream()
                .filter(Player::isAlive)
                .toList();
    }

    public boolean canStartGame() {
        return players.size() >= MIN_PLAYERS;
    }

    public int size() {
        return players.size();
    }
}