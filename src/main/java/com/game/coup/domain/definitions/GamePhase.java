package com.game.coup.domain.definitions;

public enum GamePhase {

    WAITING_FOR_ACTION,

    CHALLENGE_WINDOW,

    BLOCK_WINDOW,

    BLOCK_CHALLENGE_WINDOW,

    REVEAL_CARD,

    EXCHANGE_SELECTION,

    RESOLVING

}

/*
    // Idle,

    // ActionDeclared,

    // ChallengeWindow,
    // ChallengeInitiated,
    // ChallengeResolving,
    // ChallengeResolved,

    // BlockWindow,
    // BlockDeclared,
    // BlockResolving,
    // BlockResolved,

    // BlockChallengeWindow,
    // BlockChallengeInitiated,
    // BlockChallengeResolving,
    // BlockChallengeResolved,

    // RevealStart,
    // RevealComplete,

    // ActionExecuting,
    // ActionCompleted
*/

/*
action
challenge
block
challenge_block 

perfroming action -> exchange 

performing reveal ->
*/

/*

init
action call/ publish

call for challenges (waiting for input)
challenged -> if reveal (resolver)
challenge resolved ( perfroming action - reveal or end whatever)

call for block
blocked ->if reveal
block resolved

call for challenge block
challenged_block -> if reveal
block resolved

action started
action ended

event over -> init state

------------------------------
Reveal started
reveal ended



*/

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