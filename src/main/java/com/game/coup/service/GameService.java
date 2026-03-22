package com.game.coup.service;

import com.game.coup.domain.Game;
import com.game.coup.domain.Room;
import com.game.coup.domain.model.Player;
import com.game.coup.dto.GameMoveRequest;
import com.game.coup.dto.GameMoveResponse;
import com.game.coup.dto.GameStateRequest;
import com.game.coup.dto.GameStateResponse;
import com.game.coup.dto.debug.GameDebugResponse;
import com.game.coup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;
    private GameStateResolver gameStateResolver; // needs to be final ?
 
    public GameStateResponse getGameState(GameStateRequest request) {

       Room room =Objects.requireNonNull(
                roomRepository.getRoom(request.getRoomId()),
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

    public GameMoveResponse processGameMove(GameMoveRequest request){
        // validation of the usage of the correct state
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


// need to think of only execute once -> game level (via states)
// need to think of can perfrom state game and both turncontext is wrong  -> game check states tuen context validates the state for execution

// statrt reveal and pause action here ?? -> game context

// new card if challenge won -> game
// remove block role as options are give and the particular role will be choosed -> game

/*
For all of these add a valid in valid option check

Alive checks 
Action cost checks coup if 10+
Options for BLocker cahllenger target
also check loser 
roles belong to blocker is alive etc
*/

/*
What will my state have 

INITIAL-
Is alive cards funds (Cards to be hidden) 
turn indicator +last turn
action options (differ ppl to people based on coins and coup )
player and their coins (generic)

based on action you can choose un choose player

TURN WISE
based on state you get option 


1)
if(neither challenge blockable directly pass)
are challenge 
block with the card of choice 
pass-> action exec

2) if cahllenge direct
reveal if lost who ever turn 
ends with action done or not and give new card

3) block challenge block has the same trajectory 

note state depends per player except init
elist of who can block challenge 

reveal
ask to choose for a option 

excahnge
ask to choose between cards 

*/



// new card no show / reveal 
// option for block also not there

// mismatched reveal exchange can do it so can game so whats the point ??

// after challenge won can I block yes ofcourse

// after reveal state matters 
// validate when to perform action 
// even for block need a state of block window
// need some sort of FSM shit 

// auto state changer for window







// unified state

/*

Here is a clean Knowledge Transfer (KT) summary you can paste to start a new discussion.

⸻

KT: Multiplayer Game State Communication

Problem

A simple multiplayer game can end up with many internal states such as:
	•	Action declaration
	•	Challenge window
	•	Block window
	•	Reveal phases
	•	Resolution phases

Managing and synchronizing these states across clients can become complex.

The key question:

How do multiplayer games maintain and communicate these states efficiently?

⸻

Core Principle

Multiplayer games do not synchronize internal state machines directly between clients.

Instead they use:
	1.	Client Commands (Intent)
	2.	Server Authoritative State Machine
	3.	Server Event Broadcasts
    GameState        (turn, players, cards)
ActionContext    (current action)
GameEvent        (network messages)
InternalPhase    (server-only state machine)

CLIENT
  |
  |  command
  v
SERVER
  - authoritative state
  - validation
  - state machine
  |
  |  events
  v
CLIENTS
  - update UI
  - animations
  Client → sends INTENT
Server → runs the state machine
Server → broadcasts RESULTS
Clients → update visuals
*/

/*
{
  "phase": "CHALLENGE_WINDOW",
  "currentAction": {
    "type": "ASSASSINATE",
    "actor": "P1",
    "target": "P2"
  },
  "allowedResponses": ["CHALLENGE", "PASS"]
}

also it should have the current full state of game 
   */

/*
gamephase  once action recieved update state and on to challenge window if possible if over on to block window in possible challengeblock if possible then the end 
make resolve manual 
 */