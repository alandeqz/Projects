package kz.edu.nu.cs.se.hw;

import java.util.*;

/**
 * Starter code for a class that implements the <code>PlayableRummy</code>
 * interface. A constructor signature has been added, and method stubs have been
 * generated automatically in eclipse.
 * 
 * Before coding you should verify that you are able to run the accompanying
 * JUnit test suite <code>TestRummyCode</code>. Most of the unit tests will fail
 * initially.
 * 
 * @see PlayableRummy
 * @see TestRummyCode
 *
 */
public class Rummy implements PlayableRummy {
    private String [] players;
    private int currentPlayer;
    private Stack<String> deck = new Stack<>();
    private Stack<String> discardPile = new Stack<>();
    private Steps currentStep;
    private ArrayList<ArrayList<String>> hands = new ArrayList<>();
    private ArrayList<ArrayList<String>> melds = new ArrayList<>();
    private boolean [] drawnFromDiscard;

    public Rummy (String... players) {
        if (players.length < 2) {
            throw new RummyException ("!!! NOT ENOUGH PLAYERS !!!", 2);
        }
        else if (players.length > 6) {
            throw new RummyException ("!!! EXPECTED FEWER PLAYERS !!!", 8);
        }
        this.players = players;
        this.currentStep = Steps.WAITING;
        this.currentPlayer = 0;
        drawnFromDiscard = new boolean[players.length];
        Arrays.fill (drawnFromDiscard, false);
        String[] ranks = new String[] {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] suits = new String[] {"H", "S", "C", "D", "M"};
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.push (rank + suit);
            }
        }
    }

    @Override
    public String[] getPlayers() {
        return players;
    }

    @Override
    public int getNumPlayers() {
        return players.length;
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public int getNumCardsInDeck() {
        return deck.size();
    }

    @Override
    public int getNumCardsInDiscardPile() {
        return discardPile.size();
    }

    @Override
    public String getTopCardOfDiscardPile() {
        if (discardPile.size() <= 0) {
            throw new RummyException ("!!! NOT VALID DISCARD !!!", 13);
        }
        return discardPile.peek();
    }

    @Override
    public String[] getHandOfPlayer (int player) {
        if (player < 0 || player >= hands.size()) {
            throw new RummyException ("!!! NOT VALID INDEX OF PLAYER !!!", 10);
        }
        String [] hand = new String [hands.get (player).size()];
        for (int i = 0; i < hands.get (player).size(); ++i) {
            hand[i] = hands.get (player).get (i);
        }
        return hand;
    }

    @Override
    public int getNumMelds() {
        return melds.size();
    }

    @Override
    public String[] getMeld (int i) {
        if (i < 0 || i >= melds.size()) {
            throw new RummyException ("!!! NOT VALID INDEX OF MELD !!!", 11);
        }
        String [] meld = new String [melds.get (i).size()];
        int it = 0;
        for (String s : melds.get (i)) {
            meld[it] = s;
            ++it;
        }
        return melds.get(i).toArray(new String[melds.get(i).size()]);
    }


    @Override
    public void rearrange (String card) {
        if (currentStep != Steps.WAITING) {
            throw new RummyException ("!!! EXPECTED WAITING STEP !!!", 3);
        }
        if (deck.size() <= 0) {
            throw new RummyException ("!!! EXPECTED CARDS !!!", 7);
        }
        deck.remove (card);
        deck.push (card);
    }

    @Override
    public void shuffle (Long l) {
        if (currentStep != Steps.WAITING) {
            throw new RummyException ("!!! EXPECTED WAITING STEP !!!", 3);
        }
        Collections.shuffle (deck, new Random (l));
    }

    @Override
    public Steps getCurrentStep() {
        return currentStep;
    }

    @Override
    public int isFinished() {
        if (getCurrentStep() != Steps.FINISHED) {
            return -1;
        }
        return currentPlayer;
    }

    @Override
    public void initialDeal() {
        if (currentStep != Steps.WAITING) {
            throw new RummyException ("!!! EXPECTED WAITING STEP !!!", 3);
        }
        int distribution = 0;
        if (players.length < 2 || players.length > 6) {
            throw new RummyException ("!!! NOT ENOUGH PLAYERS !!!", 2);
        }
        if (players.length == 2) {
            distribution = 10;
        }
        else if (players.length == 3 || players.length == 4) {
            distribution = 7;
        }
        else if (players.length == 5 || players.length == 6) {
            distribution = 6;
        }
        for (int i = 0; i < players.length; ++i) {
            hands.add (new ArrayList<String>());
        }
        for (int d = 0; d < distribution; ++d) {
            for (int i = 0; i < players.length; ++i) {
                hands.get (i).addAll (new ArrayList<>(Arrays.asList(deck.peek())));
                deck.pop();
            }
        }
        discardPile.push (deck.pop());
        currentStep = Steps.DRAW;
    }

    @Override
    public void drawFromDiscard() {
        if (currentStep != Steps.DRAW) {
            throw new RummyException ("!!! EXPECTED DRAW STEP !!!", 4);
        }
        currentStep = Steps.MELD;
        hands.get (currentPlayer).add (discardPile.pop());
        drawnFromDiscard[currentPlayer] = true;
    }

    @Override
    public void drawFromDeck() {
        if (currentStep != Steps.DRAW) {
            throw new RummyException ("!!! EXPECTED DRAW STEP !!!", 4);
        }
        currentStep = Steps.MELD;
        hands.get (currentPlayer).add (deck.pop());
        drawnFromDiscard[currentPlayer] = false;
    }

    @Override
    public void meld (String... cards) {
        boolean validMeld = equalSuits (cards) && increasingRanks (cards) || equalRanks (cards) && differentSuits (cards);
        if (currentStep != Steps.MELD && currentStep != Steps.RUMMY) {
            throw new RummyException ("!!! EXPECTED MELD STEP OR RUMMY STEP !!!", 15);
        }
        if (cards.length < 3 || !validMeld) {
            throw new RummyException ("!!! NOT VALID MELD !!!", 1);
        }
        melds.add (new ArrayList<>(Arrays.asList (cards)));
        for (String card : cards) {
            hands.get (currentPlayer).remove (card);
        }
        if (currentStep == Steps.RUMMY) {
            if (cards.length == 0 || cards.length == 1) {
                currentStep = Steps.FINISHED;
            }
        }
        else if (currentStep == Steps.MELD) {
            if (cards.length == 0) {
                currentStep = Steps.FINISHED;
            }
        }

    }

    private boolean equalSuits (String... cards) {
        char suit = cards[0].charAt(1);
        for (String card : cards) {
            if (!(card.charAt(1) == suit)) {
                return false;
            }
        }
        return true;
    }

    private boolean equalRanks (String... cards) {
        char rank = cards[0].charAt(0);
        for (String card : cards) {
            if (!(card.charAt(0) == rank)) {
                return false;
            }
        }
        return true;
    }

    private boolean increasingRanks (String... cards) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (String card : cards) {
            int i = Integer.parseInt (card.substring(0, 0));
            numbers.add (i);
        }
        Collections.sort (numbers);
        int check = numbers.get (0);
        for (int number : numbers) {
            if (number != check) {
                return false;
            }
            ++check;
        }
        return true;
    }

    private boolean differentSuits (String... cards) {
        char suit = cards[0].charAt(1);
        int i = 0;
        for (String card : cards) {
            if (card.charAt(1) == suit && i != 0) {
                ++i;
                return false;
            }
        }
        return true;
    }

    @Override
    public void addToMeld (int meldIndex, String... cards) {
        if (currentStep != Steps.MELD && currentStep != Steps.RUMMY) {
            throw new RummyException ("!!! EXPECTED MELD STEP OR RUMMY STEP !!!", 15);
        }
        for (String s : cards) {
            melds.get (meldIndex).add (s);
        }
        for (String card : cards) {
            hands.get (currentPlayer).remove (card);
        }
        if (hands.get (currentPlayer).size() == 0) {
            currentStep = Steps.FINISHED;
        }
    }

    @Override
    public void declareRummy() {
        if (currentStep != Steps.MELD) {
            throw new RummyException ("!!! EXPECTED MELD STEP !!!", 5);
        }
        currentStep = Steps.RUMMY;
    }

    @Override
    public void finishMeld() {
        if (currentStep != Steps.MELD && currentStep != Steps.RUMMY) {
            throw new RummyException ("!!! EXPECTED MELD STEP OR RUMMY STEP !!!", 15);
        }
        currentStep = Steps.DISCARD;
    }

    @Override
    public void discard (String card) {
        if (currentStep != Steps.DISCARD) {
            throw new RummyException ("!!! EXPECTED DISCARD STEP !!!", 6);
        }
        if (!hands.get (currentPlayer).contains (card)) {
            throw new RummyException ("!!! EXPECTED CARDS !!!", 7);
        }
        if (drawnFromDiscard[currentPlayer]) {
            throw new RummyException("!!! NOT VALID DISCARD !!!", 13);
        }
        discardPile.push (card);
        hands.get (currentPlayer).remove (card);
        if (getHandOfPlayer(currentPlayer).length == 0) {
            currentStep = Steps.FINISHED;
        }
        else {
            if (currentPlayer != players.length - 1) {
                ++currentPlayer;
            }
            else {
                currentPlayer = 0;
            }
            currentStep = Steps.DRAW;
        }
    }
}