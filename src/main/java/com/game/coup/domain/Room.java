package com.game.coup.domain;

import java.util.*;

import com.game.coup.domain.definitions.RoomState;
import com.game.coup.domain.model.Player;

public class Room {

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;

    private final String roomId;
    private final Set<String> players ;

    private final String owner;

    private RoomState roomState = RoomState.WAITING;
    private Game game;


    public Room(String roomId, String owner) {
        this.roomId = roomId;
        this.owner = owner;
        players = new LinkedHashSet<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public Game getGame(){
        if (roomState != RoomState.STARTED)throw new IllegalStateException("Game not running.");
        return game;
    }

    public RoomState getGameState(){
        return roomState;
    }

    public void addPlayer(String player) {

        if (roomState!=RoomState.WAITING) throw new IllegalStateException("Game already started.");
        if (player == null || player.isBlank()) throw new IllegalArgumentException("Invalid player.");
        if (players.size() >= MAX_PLAYERS) throw new IllegalStateException("Maximum players reached.");
        if (players.contains(player)) throw new IllegalStateException("Player already exists.");
        
        players.add(player);
    }

    private List<Player> getPlayers() {
        return players.stream()
                  .map(Player::new)
                  .toList();
    }

    public boolean canStart() {
        return roomState == RoomState.WAITING
           && players.size() >= MIN_PLAYERS;
    }

    public void markStarted(String owner) {
        if (!canStart() ) throw new IllegalStateException("Not enough players.");
        if (roomState!=RoomState.WAITING) throw new IllegalStateException("Game already started.");
        if(!this.owner.equals(owner)) throw new IllegalArgumentException("Only owner can start");

        roomState = RoomState.STARTED;
        this.game = new Game(getPlayers());
        players.clear();
    }

    public void markEnded() {
        if (roomState!=RoomState.WAITING) throw new IllegalStateException("Game not started");
        if (roomState!=RoomState.ENDED) throw new IllegalStateException("Game already ended");
        roomState = RoomState.ENDED;
        game = null;
    }

}