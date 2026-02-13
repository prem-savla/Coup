package com.game.coup.domain;

import java.util.*;

import com.game.coup.entities.Player;

public class Room {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;

    private final String roomId;
    private final Map<String, Player> players = new LinkedHashMap<>();
    private boolean started = false;

    public Room(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void addPlayer(Player player) {

        if (started) {
            throw new IllegalStateException("Game already started.");
        }

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

    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public boolean canStart() {
        return players.size() >= MIN_PLAYERS;
    }

    public void markStarted() {
        if (!canStart()) {
            throw new IllegalStateException("Not enough players.");
        }
        this.started = true;
    }
}