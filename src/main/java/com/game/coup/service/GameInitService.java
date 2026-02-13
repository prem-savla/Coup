package com.game.coup.service;

import org.springframework.stereotype.Service;

import com.game.coup.domain.Game;
import com.game.coup.domain.Room;
import com.game.coup.entities.Player;
import com.game.coup.repository.GameRepository;
import com.game.coup.repository.RoomRepository;

import java.util.List;

@Service
public class GameInitService {

    private final RoomRepository roomRepository;
    private final GameRepository gameRepository;

    public GameInitService(RoomRepository roomRepository,
                           GameRepository gameRepository) {
        this.roomRepository = roomRepository;
        this.gameRepository = gameRepository;
    }

    public Room createRoom() {
        return roomRepository.createRoom();
    }

    public Room addPlayer(String roomId, Player player) {
        Room room = roomRepository.getRoom(roomId);
        room.addPlayer(player);
        return room;
    }

    public boolean canStart(String roomId){
        return roomRepository.getRoom(roomId).canStart();
    }

    public Game startGame(String roomId) {

        Room room = roomRepository.getRoom(roomId);
        room.markStarted();

        List<Player> players = room.getPlayers();

        Game game = new Game(players);

        gameRepository.save(game);

        roomRepository.removeRoom(roomId);

        return game;
    }
}