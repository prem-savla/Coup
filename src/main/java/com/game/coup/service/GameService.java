package com.game.coup.service;

import com.game.coup.domain.Game;
import com.game.coup.domain.Room;
import com.game.coup.domain.model.Player;
import com.game.coup.dto.debug.GameDebugResponse;
import com.game.coup.dto.request.GameMoveRequest;
import com.game.coup.dto.request.GameStateRequest;
import com.game.coup.dto.response.GameMoveResponse;
import com.game.coup.dto.response.gamestate.GameStateResponse;
import com.game.coup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;
    private final GameStateResolver gameStateResolver; // needs to be final ?
 
    public GameStateResponse getGameState(String roomId, GameStateRequest request) {

       Room room =Objects.requireNonNull(
                roomRepository.getRoom(roomId),
                "Room not found"
        );

        Game game = Objects.requireNonNull(
                room.getGame(),
                "Game not started"
        );

        Player viewer = game.getPlayerByName(request.getPlayerName());
        // non null?

        return gameStateResolver.resolve(game, viewer); 
    }

    public GameMoveResponse processGameMove(String roomId, GameMoveRequest request){
        // validation of the usage of the correct state and call game
        return null;
    }

    public GameDebugResponse getDebugState(String roomId) {

        Room room =Objects.requireNonNull(
                roomRepository.getRoom(roomId),
                "Room not found"
        );

        Game game = Objects.requireNonNull(
                room.getGame(),
                "Game not started"
        );

        return GameDebugResponse.from(game);
    }
}
/*
KT: Multiplayer Game State & Flow (Simplified)

General Rules:
- All validations at game level:
  - alive checks
  - action cost (e.g., coup if ≥10 coins mandatory)
  - valid options (challenge/block/target)
  - role ownership (blocker must have valid alive role)
- TurnContext should not control flow, only assist execution
- Game controls phases + transitions
- Actions should execute only once (state-driven)

State Structure:

INITIAL (Global State):
- players (coins, alive, hidden cards)
- turn indicator + last turn
- available actions per player (based on coins/state)
- target selection (if required)


Special Notes:
- Reveal is a controlled sub-state (can be triggered by game, not player arbitrarily)
- Exchange and reveal can overlap conceptually → must be separated by state
- After challenge success, block is still allowed (rules permit)
- Block must have its own window state
- Options are always state-dependent and player-specific

FSM Requirement:
- Strict InternalPhase machine (server-side only)
- Automatic transitions for timed windows (challenge/block)
- Manual resolution step for clarity/control

Example State Payload:

{
  "phase": "CHALLENGE_WINDOW",
  "currentAction": {
    "type": "ASSASSINATE",
    "actor": "P1",
    "target": "P2"
  },
  "allowedResponses": ["CHALLENGE", "PASS"],
  "gameState": { ...full snapshot... }
}
*/