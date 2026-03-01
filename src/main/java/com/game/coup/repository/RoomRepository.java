package com.game.coup.repository;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.game.coup.domain.Room;

@Repository
public class RoomRepository {

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    private final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public Room createRoom(String owner) {
        // String roomId = generateRoomId();
        String roomId = "ABCD";
        Room room = new Room(roomId, owner);
        if (rooms.putIfAbsent(roomId, room) != null) {
            throw new IllegalStateException("Room already exists: " + roomId);
        }
        return room;
    }

    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
        return room;
    }

    public void removeRoom(String roomId) {
        if (rooms.remove(roomId) == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
    } 
    
    private String generateRoomId() {
        while (true) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) sb.append(CHARACTERS.charAt(random.nextInt(26)));
            String roomId = sb.toString();
            if (!rooms.containsKey(roomId)) return roomId;
        }
    }
}