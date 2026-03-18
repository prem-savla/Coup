package com.game.coup.domain.definitions;

// for backend internal states
public enum GamePhase {

    IDLE(GameState.AWAITING_ACTION),

    // ACTION
    ACTION_DECLARED(GameState.CHALLENGE_WINDOW),

    // CHALLENGE (ACTION)
    CHALLENGE_WINDOW(GameState.CHALLENGE_WINDOW),
    CHALLENGE_INITIATED(GameState.RESOLVING),
    CHALLENGE_RESOLVING(GameState.RESOLVING),

    // BLOCK
    BLOCK_WINDOW(GameState.BLOCK_WINDOW),
    BLOCK_DECLARED(GameState.BLOCK_CHALLENGE_WINDOW),
    BLOCK_RESOLVING(GameState.RESOLVING),

    // BLOCK CHALLENGE
    BLOCK_CHALLENGE_WINDOW(GameState.BLOCK_CHALLENGE_WINDOW),
    BLOCK_CHALLENGE_INITIATED(GameState.RESOLVING),
    BLOCK_CHALLENGE_RESOLVING(GameState.RESOLVING),

    // REVEAL
    REVEAL_INITIATED(GameState.AWAITING_REVEAL),
    REVEAL_SELECTION(GameState.AWAITING_REVEAL),
    REVEAL_COMPLETE(GameState.RESOLVING),

    // EXCHANGE
    EXCHANGE_START(GameState.AWAITING_EXCHANGE),
    EXCHANGE_SELECTION(GameState.AWAITING_EXCHANGE),
    EXCHANGE_COMPLETE(GameState.RESOLVING),

    // FINAL ACTION
    ACTION_EXECUTING(GameState.RESOLVING),
    ACTION_COMPLETED(GameState.AWAITING_ACTION),

    GAME_OVER(GameState.GAME_OVER);


    private final GameState gameState;

    GamePhase(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isResolving() {
        return gameState == GameState.RESOLVING;
    }
}




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