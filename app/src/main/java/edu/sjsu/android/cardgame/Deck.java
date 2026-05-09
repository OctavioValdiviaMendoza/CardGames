package edu.sjsu.android.cardgame;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private static List<Card> masterDeck = new ArrayList<>();
    private List<Card> cards = new ArrayList<>();

    static {
        String[] suits = {"clubs", "hearts", "diamonds", "spades"};
        String[] ranks = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

        for (String suit : suits) {
            for (String rank : ranks) {
                // Initialize the master list once
                masterDeck.add(new Card(suit, rank, 0));
            }
        }
    }

    public Deck() {
        reset();
    }

    public void reset() {
        cards.clear();
        cards.addAll(masterDeck);

        for (Card card : cards) {
            card.setFaceUp(true);
        }

        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    // Draw a card from the deck
    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    // Check the size of the deck
    public int size() {
        return cards.size();
    }
}
