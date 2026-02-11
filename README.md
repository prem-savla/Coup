# Coup — Multiplayer Backend (Work in Progress)

A backend implementation of the board game **Coup**, built with Spring Boot and Redis, focused on clean architecture, strict rule enforcement, and scalable multiplayer support.

---

## 🎲 What is Coup?

Coup is a turn-based bluffing and strategy game set in a dystopian future.

Players secretly control character roles, claim powerful actions, challenge or block opponents, and attempt to eliminate others through deception and strategy.

A player loses when all influence is revealed.  
The last remaining player wins.

---

## 🧠 Project Overview

This project implements Coup as a **multiplayer backend system** with:

- Strict rule validation
- Role-based action enforcement
- Challenge and counteraction mechanics
- Influence reveal and elimination logic
- Per-room isolated game state
- Redis-backed persistence
- Dockerized infrastructure

The goal is to build a **scalable backend service** that can power a real-time multiplayer web client.

---

## 🏗 Architecture

The project follows a layered design:

### Domain Layer
Core game logic:
- `Deck`
- `Player`
- `Treasury`
- `GameContext`
- Role and action validation rules

### DTO Layer
Immutable transport objects used for Redis serialization.

### Service Layer
Game orchestration and state transitions.

### Controller Layer
REST and WebSocket endpoints (in progress).

### Redis
Each game room stores a complete game context under: