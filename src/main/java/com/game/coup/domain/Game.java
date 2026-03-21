package com.game.coup.domain;

import java.util.List;
import java.util.Optional;

import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Deck;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;
import com.game.coup.domain.turn.Executioner;
import com.game.coup.domain.turn.TurnContext;

import lombok.NonNull;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.GamePhase;
import com.game.coup.domain.definitions.GameState;

public class Game {
    private final List<Player> players;
    private final Deck deck;
    private final Treasury treasury;

    private TurnContext ctx;

    private int currentTurnIndex;

    private GamePhase gamePhase;


    public Game(List<Player> players){
        this.players = List.copyOf(players);

        this.deck = new Deck();
        this.treasury = new Treasury();

        this.currentTurnIndex = 0;
        this.gamePhase = GamePhase.IDLE;

        dealInitialCards();
        distributeInitialCoins();

    }

    private void dealInitialCards() {
        for (Player player : players) {
            player.addCards(deck.dealCards(2));
        }
    }

    private void distributeInitialCoins() {
        for (Player player : players) {
            treasury.giveCoins(player, 2);
        }
    }

    //--- getters ---

    // public GameState getGameState() {return gamePhase.getGameState();}

    public Treasury getTreasury(){ return treasury;}

    public Deck getDeck(){ return deck;}

    public TurnContext getTurnContext() {return ctx;}

    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException("Player not found: " + name));
    }

    public List<Player> getAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .toList();
    }

    //--- Context mgm ---

    private void createContext(@NonNull Player actor,@NonNull ActionType action,@NonNull Player target) {
        if(target.equals(Player.NONE)) ctx = new TurnContext(actor, action);
        else ctx = new TurnContext(actor, action, target);
    }

    private void destroyContext() {
        ctx = null;
    }

    //--- action ---
    public void startAction(@NonNull Player actor,@NonNull ActionType action) {
        createContext(actor, action, Player.NONE);
        gamePhase = GamePhase.CHALLENGE_OR_BLOCK_WINDOW;
        

    }
    public void startTargetedAction(@NonNull Player actor,@NonNull ActionType action,@NonNull Player target) {
        createContext(actor, action, target);
        gamePhase = GamePhase.CHALLENGE_OR_BLOCK_WINDOW;
    }

    // --- challenge ---
    public void challengeAction(@NonNull Player challenger) {
        ctx.setChallenger(challenger);
        Optional<Card> provenCard = Executioner.executeChallenge(this);
        Player loser = ctx.getActionChallengeLoser();
        if (loser.equals(challenger)) {
            if (provenCard.isEmpty()) throw new IllegalStateException("Expected proven card");
            Card card = provenCard.get();
            List<Card> cardsDrawn = deck.dealCards(1);
            List<Card> cardsReturned = List.of(card);
            ctx.getActor().exchangeCards(cardsDrawn, cardsReturned);
            deck.returnCards(cardsReturned);
        }
        //reveal for loser

    }

    //--- block ---
    public void blockAction(@NonNull Player blocker) {
        ctx.setBlocker(blocker);
    }

    //--- block challenge ---

    public void challengeBlock(@NonNull Player blockChallenger) {
        ctx.setBlockChallenger(blockChallenger);
        Optional<Card> provenCard = Executioner.executeBlockChallenge(this);
        Player loser = ctx.getBlockChallengeLoser();
        if (loser.equals(blockChallenger)) {
            if (provenCard.isEmpty()) throw new IllegalStateException("Expected proven card");
            Card card = provenCard.get();
            List<Card> cardsDrawn = deck.dealCards(1);
            List<Card> cardsReturned = List.of(card);
            ctx.getBlocker().exchangeCards(cardsDrawn, cardsReturned);
            deck.returnCards(cardsReturned);
        }
        //reveal for loser

    }

    // --- reveal ---

    public String startReveal(Player player){
        return player.getName();
        // call game service 
    }

    public void executeReveal(Player player, Card revealedCard){
        player.revealCard(revealedCard);
    }

    // --- exchange action ---

    public List<Card>  startExchange() {
        return deck.dealCards(2);
        //call game service 
    }

    public void executeExchange(List<Card> cardsDrawn, List<Card> cardsReturned){
        ctx.getActor().exchangeCards(cardsDrawn, cardsReturned);
        deck.returnCards(cardsReturned);
    }

    // --- execution ---

    public void executeAction(){
        Executioner.executeAction(this);
        nextTurn();
        destroyContext();
    }

    //--- player & turn ---
    public void nextTurn() {
        if(getAlivePlayers().size() <= 1) gamePhase = GamePhase.GAME_OVER;
        else{
            do {
                currentTurnIndex = (currentTurnIndex + 1) % players.size();
            } while (!players.get(currentTurnIndex).isAlive());
            // event = null;
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }


    // --- utils --- 

    public boolean isGameOver() {
        return gamePhase == GamePhase.GAME_OVER;
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
