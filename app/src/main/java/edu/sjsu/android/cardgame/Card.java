package edu.sjsu.android.cardgame;

import android.content.Context;
import android.graphics.Color;

public class Card {
    private String suit;
    private String rank;
    private int cardID;
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
        if(rank.equals("1") || rank.equals("ace")) {
            return 11;
        }
        try {
            return Integer.parseInt(rank);
        }
        catch (NumberFormatException e) {
            return 0; // Fallback to prevent crash
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
            case "1":
            case "ace":   return "ace";
            case "jack":  return "jack";
            case "queen": return "queen";
            case "king":  return "king";
            default:      return rank; // Returns "2", "3", ... "10"
        }
    }

    public static int getThemeColor(Context context) {
        return context.getSharedPreferences("game_data", Context.MODE_PRIVATE)
                .getInt("current_theme_color", Color.TRANSPARENT);
    }
}
