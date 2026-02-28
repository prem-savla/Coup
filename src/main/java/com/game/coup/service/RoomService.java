package com.game.coup.service;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Room;
import com.game.coup.domain.model.Player;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String createRoom(String playerName) {

        String roomId = generateRoomId();

        Room room = new Room(roomId);
        room.addPlayer(new Player(playerName));

        rooms.put(roomId, room);

        return roomId;
    }

    public void joinRoom(String roomId, String playerName) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }

        room.addPlayer(new Player(playerName));
    }

    private String generateRoomId() {

        while (true) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < 4; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(26)));
            }

            String roomId = sb.toString();

            if (!rooms.containsKey(roomId)) {
                return roomId;
            }
        }
    }
}