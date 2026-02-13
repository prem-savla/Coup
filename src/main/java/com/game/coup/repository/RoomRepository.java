package com.game.coup.repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.game.coup.domain.Room;

@Repository
public class RoomRepository {

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom() {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId);
        rooms.put(roomId, room);
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
        rooms.remove(roomId);
    }
}