# Coup — Multiplayer Backend

A backend implementation of the board game **Coup**, built with **Spring Boot** and Dockerized for easy setup. The server enforces strict game rules, handles all role-based actions, challenge/counteraction mechanics, and manages isolated per-room game state.

---

## 🎲 What is Coup?

You are the head of a family in an Italian city-state. You need to manipulate, bluff, and bribe your way to power. Your objective is to destroy the influence of all other families, forcing them into exile. **Only one family will survive.**

Each player starts with **2 coins** and **2 character cards** (influence). Lose both cards and you're eliminated. The last player standing wins.

Players can claim *any* character action on their turn — whether they hold that card or not. That's the bluff. If no one challenges, the action succeeds. If challenged and you can't prove it, you lose an influence card.

---

## 🚀 Getting Started

The only requirement is **Docker**. No local Java or database setup needed.

```bash
docker compose up --build
```

The server starts on **`http://localhost:8080`**.

---

## 📡 API Reference

All requests use JSON bodies. Below are the full Postman-style examples for running a complete game.

---

### Room Management

**Endpoint:** `POST /api/room`

#### Create a Room

```json
{
  "action": "CREATE",
  "playerName": "Prem"
}
```

**Response:**
```json
{
  "roomId": "ABCD",
  "message": "Room created successfully."
}
```

---

#### Join a Room

```json
{
  "action": "JOIN",
  "playerName": "Rahul",
  "roomId": "ABCD"
}
```

**Response:**
```json
{
  "roomId": "ABCD",
  "message": "Joined room successfully."
}
```

---

#### Start the Game

Only the room creator can start the game.

```json
{
  "action": "START",
  "playerName": "Prem",
  "roomId": "ABCD"
}
```

**Response:**
```json
{
  "roomId": "ABCD",
  "message": "Game Started."
}
```

---

### Game State

**Endpoint:** `GET /api/{roomId}/state`

Returns the current game state visible to the requesting player — their own cards and coins, and public info for other players.

**Body:**
```json
{
  "playerName": "Rahul"
}
```

**Example Response:**
```json
{
  "gameState": {
    "phase": "EXCHANGE",
    "currentPlayer": "Rahul",
    "action": "EXCHANGE",
    "actor": "Rahul",
    "target": "__NONE__",
    "challenger": "__NONE__",
    "blocker": "__NONE__",
    "blockChallenger": "__NONE__"
  },
  "playersState": {
    "self": {
      "name": "Rahul",
      "coins": 2,
      "playingCards": ["ASSASSIN", "ASSASSIN"],
      "revealedCards": []
    },
    "otherPlayers": [
      {
        "name": "Prem",
        "coins": 3,
        "revealedCards": []
      }
    ]
  },
  "playerOptions": {
    "actions": null,
    "responses": null,
    "reveal": null,
    "exchange": {
      "drawnCards": [
        { "type": "AMBASSADOR", "id": "1b2f7642-a819-4f7f-9cf1-fdf1ba265745" },
        { "type": "CAPTAIN",    "id": "32692056-ab00-4f57-947f-de67ae9aff0d" }
      ],
      "playingCards": [
        { "type": "ASSASSIN", "id": "691ba675-a2c0-41e3-b4dc-19fba0d6b6c4" },
        { "type": "ASSASSIN", "id": "06422b29-0205-4405-aa16-96ea80f2c277" }
      ],
      "selectCount": 2
    }
  }
}
```

---

### Debug State

**Endpoint:** `GET /api/{roomId}/debug`

Returns the full internal game state — all player cards and coins — for debugging purposes.

No body required.

---

### Make a Move

**Endpoint:** `POST /api/{roomId}/move`

Used for all in-game actions including regular moves, challenge/block responses, card reveals, and the Ambassador exchange.

#### Standard Move (no exchange)

```json
{
  "playerName": "Rahul",
  "choice": "INCOME",
  "target": null,
  "drawnCards": null,
  "returnedCards": null,
  "revealCard": null
}
```

#### Ambassador Card Exchange (SWAP_CARDS)

During an Ambassador exchange, the player receives drawn cards from the deck, sees their own playing cards, and must return cards equal to `selectCount`.

```json
{
  "playerName": "Rahul",
  "choice": "SWAP_CARDS",
  "target": null,
  "drawnCards": [
    { "type": "AMBASSADOR", "id": "1b2f7642-a819-4f7f-9cf1-fdf1ba265745" },
    { "type": "ASSASSIN",   "id": "06422b29-0205-4405-aa16-96ea80f2c277" }
  ],
  "returnedCards": [
    { "type": "ASSASSIN", "id": "691ba675-a2c0-41e3-b4dc-19fba0d6b6c4" },
    { "type": "CAPTAIN",  "id": "32692056-ab00-4f57-947f-de67ae9aff0d" }
  ],
  "revealCard": null
}
```

> **How exchange works:** The `drawnCards` field contains the cards drawn from the deck (shown in `playerOptions.exchange.drawnCards`). The `returnedCards` field contains the cards the player wants to give back — selecting from the combined pool of drawn + playing cards. The cards kept are whatever is not returned.

---

## 🏗 Architecture

The project follows a clean layered design:

| Layer | Responsibility |
|---|---|
| **Domain** | Core game logic — `Deck`, `Player`, `Treasury`, `TurnContext`, role and action rules |
| **DTO** | Immutable transport objects for request/response serialization |
| **Service** | Game orchestration and state transitions |
| **Controller** | REST endpoints and global exception handling |
| **Repository** | In-memory room storage |

---

## 📁 Project Structure

```
src/main/java/com/game/coup/
├── CoupGameApplication.java
├── controller/
│   ├── RoomController.java          # Room create / join / start
│   ├── GameController.java          # Game state and move endpoints
│   └── GlobalExceptionHandler.java
├── domain/
│   ├── Game.java
│   ├── Room.java
│   ├── definitions/
│   │   ├── ActionType.java          # INCOME, FOREIGN_AID, COUP, TAX, STEAL, ASSASSINATE, EXCHANGE
│   │   ├── GamePhase.java           # Turn phases (ACTION, BLOCK, CHALLENGE, REVEAL, EXCHANGE…)
│   │   ├── Role.java                # DUKE, ASSASSIN, CAPTAIN, AMBASSADOR, CONTESSA
│   │   └── RoomState.java           # WAITING, IN_PROGRESS
│   ├── model/
│   │   ├── Card.java
│   │   ├── Deck.java
│   │   ├── Player.java
│   │   └── Treasury.java
│   └── turn/
│       ├── TurnContext.java          # Tracks current turn state
│       └── Executioner.java          # Resolves turn outcomes
├── dto/
│   ├── debug/                        # Debug response shapes
│   ├── request/                      # Incoming request DTOs
│   └── response/gamestate/           # Game state response shapes
│       ├── game/                     # GameState
│       ├── option/                   # ActionOption, ResponseOption, RevealOption, ExchangeOption
│       └── player/                   # PlayerInfo, OtherPlayerInfo, PlayersState
├── repository/
│   └── RoomRepository.java           # In-memory room store
└── service/
    ├── RoomService.java
    ├── GameService.java
    ├── GameStateResolver.java        # Builds per-player game state view
    └── GameMoveResolver.java         # Validates and applies moves
```

---

## 🃏 Roles & Actions

### Character Actions

| Role | Action | Block Ability |
|---|---|---|
| **Duke** | Tax — take 3 coins from the treasury | Blocks Foreign Aid |
| **Assassin** | Assassinate — pay 3 coins, force a target to lose an influence | — |
| **Captain** | Steal — take 2 coins from another player | Blocks Stealing |
| **Ambassador** | Exchange — draw 2 cards from the deck, keep or swap with your own, return the rest | Blocks Stealing |
| **Contessa** | — | Blocks Assassination |

> Any player can *claim* any character action regardless of what cards they actually hold. If unchallenged, the action succeeds. That's the bluff.

### General Actions (no character required, cannot be challenged)

| Action | Effect |
|---|---|
| **Income** | Take 1 coin from the treasury |
| **Foreign Aid** | Take 2 coins from the treasury (blockable by Duke) |
| **Coup** | Pay 7 coins — force a target to immediately lose an influence. Cannot be blocked. **Mandatory if you hold 10+ coins.** |

---

## 🔁 Game Flow

```
Player takes action
    └─ Other players may CHALLENGE or BLOCK
            ├─ CHALLENGE: If the actor doesn't hold the claimed role → reveals a card and loses influence
            │             If the actor does hold it → challenger loses a card; actor shuffles and draws new card
            └─ BLOCK: Blocker claims a counter-role
                    └─ Others may challenge the block
                            ├─ Block stands → action is cancelled
                            └─ Block fails → blocker loses influence, original action resolves
```
