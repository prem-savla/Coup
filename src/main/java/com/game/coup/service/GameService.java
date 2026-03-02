package com.game.coup.service;

import com.game.coup.domain.Game;
import com.game.coup.domain.Room;
import com.game.coup.domain.definitions.FlowState;
import com.game.coup.dto.GameFlowRequest;
import com.game.coup.dto.debug.GameDebugResponse;
import com.game.coup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;

    public void handleFlow(GameFlowRequest request) {

        Room room = roomRepository.getRoom(request.getRoomId());

        if (room == null) {
            throw new IllegalStateException("Room not found");
        }

        Game game = room.getGame();

        if (game == null) {
            throw new IllegalStateException("Game not started");
        }

        // Start action
        if (request.getActionType() != null) {

            if (request.getTargetName() != null) {
                game.performTargetedAction(
                        request.getActionType(),
                        request.getTargetName()
                );
            } else {
                game.performAction(
                        request.getActionType()
                );
            }
        }

        // Counter / Resolve
        else if (request.getFlowState() != null) {

            if(request.getFlowState() == FlowState.RESOLVE) 
                game.performResolve(request.getFlowState());
            else{
                game.performCounters(
                        request.getFlowState(),
                        request.getPlayerName()
                );
            }
        }

        else {
            throw new IllegalArgumentException("Invalid request");
        }

    }

    public Object getOptions(String roomId) {

        Room room = roomRepository.getRoom(roomId);

        if (room == null) {
            throw new IllegalStateException("Room not found");
        }

        Game game = room.getGame();

        if (game == null ) {
            return null;
        }

        return game.getOptions();
    }

    public GameDebugResponse getDebugState(String roomId) {

        Room room = roomRepository.getRoom(roomId);

        if (room == null) {
            throw new IllegalStateException("Room not found");
        }

        Game game = room.getGame();

        if (game == null) {
            throw new IllegalStateException("Game not started");
        }

        return GameDebugResponse.from(game);
    }
}