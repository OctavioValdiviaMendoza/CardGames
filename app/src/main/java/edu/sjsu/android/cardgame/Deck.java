package edu.sjsu.android.cardgame;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards = new ArrayList<>();
    public Deck(Context context) {
        String[] suits = {"clubs", "hearts", "diamonds", "spades"};
        String[] ranks = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

        for (String suit : suits) {
            for (String rank : ranks) {
                String drawableName = suit + "_" + rank;
                int resId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
                cards.add(new Card(suit, rank, resId));
            }
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
