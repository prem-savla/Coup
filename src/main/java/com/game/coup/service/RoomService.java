package com.game.coup.service;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Room;
import com.game.coup.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public String createRoom(String playerName) {
        Room room = repository.createRoom(playerName);
        room.addPlayer(playerName);
        return room.getRoomId();
    }

    public void joinRoom(String roomId, String playerName) {
        Room room = repository.getRoom(roomId);
        room.addPlayer(playerName);
    }

    public void startGame(String roomId, String owner){
        Room room = repository.getRoom(roomId);
        room.markStarted(owner);
    }
    
}