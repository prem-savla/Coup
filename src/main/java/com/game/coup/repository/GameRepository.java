package com.game.coup.repository;


import org.springframework.stereotype.Repository;

import com.game.coup.domain.Game;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class GameRepository {

    private final ConcurrentMap<String, Game> games = new ConcurrentHashMap<>();

    public Game findByRoomId(String roomId) {
        Game game = games.get(roomId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + roomId);
        }
        return game;
    }
    
    public void save(Game game) {
        games.put(game.getRoomId(), game);
    }
    
    public void delete(String roomId) {
        games.remove(roomId);
    }
    
    public boolean exists(String roomId) {
        return games.containsKey(roomId);
    }

    public boolean exists(Game game) {
        return games.containsKey(game.getRoomId());
    }
}