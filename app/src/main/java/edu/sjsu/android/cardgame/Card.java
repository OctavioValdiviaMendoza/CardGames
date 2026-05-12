package edu.sjsu.android.cardgame;

import android.content.Context;
import android.graphics.Color;

public class Card {
    private final String suit;
    private final String rank;
    private final int cardID;
    private boolean isFaceUp;

    public Card(String suit, String rank, int cardID) {
        this.suit = suit;
        this.rank = rank;
        this.cardID = cardID;
        isFaceUp = true;
    }

    public int getValue() {
        if(rank.equals("jack") || rank.equals("queen") || rank.equals("king")) {
            return 10;
        }
        if(rank.equals("ace")) {
            return 11;
        }
        try {
            return Integer.parseInt(rank);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getCardID() {
        return cardID;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public void setFaceUp(boolean isFaceUp) {
        this.isFaceUp = isFaceUp;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public String getRankLabel() {
        switch (rank.toLowerCase()) {
            case "ace":
                return "ace";
            case "jack":
                return "jack";
            case "queen":
                return "queen";
            case "king":
                return "king";
            default:
                return rank;
        }
    }

    public static int getThemeColor(Context context) {
        return context.getSharedPreferences("game_data", Context.MODE_PRIVATE)
                .getInt("current_theme_color", Color.TRANSPARENT);
    }
}
