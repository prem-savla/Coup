package com.game.coup.domain;

import java.util.List;

import com.game.coup.domain.model.Card;
import com.game.coup.domain.model.Deck;
import com.game.coup.domain.model.Player;
import com.game.coup.domain.model.Treasury;
import com.game.coup.domain.turn.Executioner;
import com.game.coup.domain.turn.TurnContext;

import lombok.NonNull;

import com.game.coup.domain.definitions.ActionType;
import com.game.coup.domain.definitions.GamePhase;

public class Game {
    private final List<Player> players;
    private final Deck deck;
    private final Treasury treasury;

    private TurnContext ctx;

    private int currentTurnIndex;

    private GamePhase gamePhase;
    private GamePhase prevPhase; // used to resolve the reveal action


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

    // --- Getters ---

    public Treasury getTreasury(){ return treasury;}

    public Deck getDeck(){ return deck;}

    public TurnContext getTurnContext() {return ctx;}

    public GamePhase getGamePhase(){return gamePhase;}

    public Player getCurrentPlayer() {return players.get(currentTurnIndex);}

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

    // --- Context mgm ---

    private void createContext(@NonNull Player actor,@NonNull ActionType action,@NonNull Player target) {
        if(gamePhase == GamePhase.GAME_OVER) throw new IllegalStateException("GAME OVER");
        if(target.equals(Player.NONE)) ctx = new TurnContext(actor, action);
        else ctx = new TurnContext(actor, action, target);
    }

    private void destroyContext() {
        ctx = null;
    }

    // --- Action ---

    public void startAction(@NonNull Player actor,@NonNull ActionType action) {
        if(gamePhase!=GamePhase.IDLE) throw new IllegalStateException(gamePhase.toString());
        createContext(actor, action, Player.NONE);
        set1GamePhase();
        

    }
    public void startTargetedAction(@NonNull Player actor,@NonNull ActionType action,@NonNull Player target) {
        if(gamePhase!=GamePhase.IDLE) throw new IllegalStateException(gamePhase.toString());
        createContext(actor, action, target);
        set1GamePhase();
    }

    // --- Challenge ---

    public void challengeAction(@NonNull Player challenger) {
        if(gamePhase!=GamePhase.CHALLENGE_WINDOW) throw new IllegalStateException(gamePhase.toString());
        ctx.setChallenger(challenger);
        Executioner.executeChallenge(this);
        Player loser = ctx.getActionChallengeLoser();
        startReveal(loser);  
    }

    public void noChallengeAction(){
        if(gamePhase!=GamePhase.CHALLENGE_WINDOW) throw new IllegalStateException(gamePhase.toString());
        set2GamePhase();
    }

    // --- Block ---

    public void blockAction(@NonNull Player blocker) {
        if(gamePhase !=GamePhase.BLOCK_WINDOW) throw new IllegalStateException(gamePhase.toString());
        ctx.setBlocker(blocker);
        setGamePhase(GamePhase.BLOCK_CHALLENGE_WINDOW);
    }

    public void noBlockAction(){
        if(gamePhase!=GamePhase.BLOCK_WINDOW) throw new IllegalStateException(gamePhase.toString());
        setGamePhase(GamePhase.RESOLVE);
    }

    // --- Block challenge ---

    public void challengeBlock(@NonNull Player blockChallenger) {
        if(gamePhase!=GamePhase.BLOCK_CHALLENGE_WINDOW) throw new IllegalStateException(gamePhase.toString());
        ctx.setBlockChallenger(blockChallenger);
        Executioner.executeBlockChallenge(this);
        Player loser = ctx.getBlockChallengeLoser();
        startReveal(loser);
    }

    public void noChallengeBlock(){
        if(gamePhase!=GamePhase.BLOCK_CHALLENGE_WINDOW) throw new IllegalStateException(gamePhase.toString());
        setGamePhase(GamePhase.RESOLVE);
    }

    // --- Reveal ---

    private String startReveal(Player player){
        gamePhase = GamePhase.REVEAL;
        return player.getName();
        // call game service 
    }

    public void executeReveal(Player player, Card revealedCard){
        if(gamePhase != GamePhase.REVEAL) throw new IllegalStateException(gamePhase.toString());
        player.revealCard(revealedCard);

        switch (prevPhase) {
            case CHALLENGE_WINDOW:
                if(ctx.getActionChallengeLoser().equals(ctx.getActor())) setGamePhase(GamePhase.RESOLVE);
                else set2GamePhase();
                break;
            case BLOCK_CHALLENGE_WINDOW:
                setGamePhase(GamePhase.RESOLVE);
                break;
            case RESOLVE:
                setGamePhase(GamePhase.IDLE);
                break;
            default:
                break;
        }
    }

    // --- Exchange action ---

    private List<Card>  startExchange() {
        setGamePhase(GamePhase.EXCHANGE);
        return deck.dealCards(2);
        //call game service 
    }

    public void executeExchange(List<Card> cardsDrawn, List<Card> cardsReturned){
        if(gamePhase != GamePhase.EXCHANGE) throw new IllegalStateException(gamePhase.toString());
        ctx.getActor().exchangeCards(cardsDrawn, cardsReturned);
        deck.returnCards(cardsReturned);
        setGamePhase(GamePhase.IDLE);
    }

    // --- Execution ---

    private void executeAction(){
        if(gamePhase != GamePhase.RESOLVE) throw new IllegalStateException(gamePhase.toString());

        switch (ctx.getAction()) {
            case EXCHANGE:
                startExchange();
                break;
            case ASSASSINATE:
                startReveal(ctx.getTarget());
                break;
            case COUP:
                startReveal(ctx.getTarget());
                break;
            default:
                Executioner.executeAction(this);
                setGamePhase(GamePhase.IDLE);
                break;
        }
    }

    // --- Utils --- 

    // these 1 & 2 functions help pass phase based on action type 
    private void set1GamePhase(){
        if(!ctx.getAction().challengeable && !ctx.getAction().blockable) setGamePhase(GamePhase.RESOLVE);
        else if (!ctx.getAction().challengeable) setGamePhase(GamePhase.BLOCK_WINDOW);
        else setGamePhase(GamePhase.CHALLENGE_WINDOW);
    }

    private void set2GamePhase(){
        if(!ctx.getAction().blockable) setGamePhase(GamePhase.RESOLVE);
        else setGamePhase(GamePhase.BLOCK_WINDOW);
    }

    private void setGamePhase(GamePhase phase){
        if(phase == GamePhase.IDLE){
            nextTurn();
            destroyContext();
        }else if(phase == GamePhase.RESOLVE){
            executeAction();
        }
        gamePhase = phase;
        prevPhase = phase;
    }

    private void nextTurn() {
        if(getAlivePlayers().size() <= 1) gamePhase = GamePhase.GAME_OVER;
        else{
            do {
                currentTurnIndex = (currentTurnIndex + 1) % players.size();
            } while (!players.get(currentTurnIndex).isAlive());
        }
    }

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



// new card no show / reveal 
// option for block also not there

// mismatched reveal exchange can do it so can game so whats the point ??

// after challenge won can I block yes ofcourse

// after reveal state matters 
// validate when to perform action 
// even for block need a state of block window
// need some sort of FSM shit 

// auto state changer for window