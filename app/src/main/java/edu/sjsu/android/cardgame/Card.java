package edu.sjsu.android.cardgame;

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

    // According to BlackJack rules
    public int getValue() {
        if(rank.equals("jack") || rank.equals("queen") || rank.equals("king")) {
            return 10;
        }
        if(rank.equals("1")) {
            return 11;
        }
        return Integer.parseInt(rank);
    }

    public int getCardID() {
        return cardID;
    }

    public void setFaceUp(boolean isFaceUp) {
        this.isFaceUp = isFaceUp;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }
}
