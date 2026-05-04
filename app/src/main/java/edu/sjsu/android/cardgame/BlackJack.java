package edu.sjsu.android.cardgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackJack extends AppCompatActivity {
    private Deck deck;
    private LinearLayout playerHand;
    private LinearLayout dealerHand;
    private List<Card> playerCardList = new ArrayList<>();
    private List<Card> dealerCardList = new ArrayList<>();
    private Button btnHit;
    private Button btnStand;

    private TextView playerScoreText;
    private TextView dealerScoreText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_black_jack);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playerScoreText = findViewById(R.id.player_score);
        dealerScoreText = findViewById(R.id.dealer_score);

        playerHand = findViewById(R.id.player_hand);
        dealerHand = findViewById(R.id.dealer_hand);
        btnHit = findViewById(R.id.btn_hit);
        btnStand = findViewById(R.id.btn_stand);

        deck = new Deck(this);
        dealHands();
        updateScores(false);

        // Hit button
        // Draws another random card to the player's hand
        btnHit.setOnClickListener(v -> {
            Card card = deck.draw();

            if (card != null) {
                drawCard(playerHand, card);
                updateScores(false);
                int score = calculateScore(playerCardList);
                if (score > 21) {
                    btnHit.setEnabled(false);
                    btnStand.setEnabled(false);
                    determineWinner();
                }
            }
        });

        // Stand button
        btnStand.setOnClickListener(v -> {
            btnHit.setEnabled(false);
            btnStand.setEnabled(false);
            dealerTurn();
        });
    }

    private void dealHands() {
        drawCard(playerHand, deck.draw());
        drawCard(playerHand, deck.draw());

        drawCard(dealerHand, deck.draw());

        Card hiddenCard = deck.draw();
        hiddenCard.setFaceUp(false);
        drawCard(dealerHand, hiddenCard);

        if (calculateScore(playerCardList) == 21) {
            btnHit.setEnabled(false);
            btnStand.setEnabled(false);
            dealerTurn();
        }
    }

    // Draws a card to the hand
    private void drawCard(LinearLayout hand, Card card) {
        ImageView cardView = new ImageView(this);
        if (card.isFaceUp()) {
            cardView.setImageResource(card.getCardID());
        }
        else {
            cardView.setImageResource(R.drawable.cardback);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 350);

        // Used to overlap the cards in the hand
        if (hand.getChildCount() > 0) {
            params.setMargins(-100, 0, 0, 0);
        }

        cardView.setLayoutParams(params);
        hand.addView(cardView);

        // Adds the card to the list according to which hand it is
        if (hand == playerHand) {
            playerCardList.add(card);
        }
        else {
            dealerCardList.add(card);
        }
    }

    // Calculates the values of all the cards in the hand
    // All ace cards have a default value of 11
    // If the total value of all cards is greater than 21, the score
    // will be subtracted by 10 (changes an ace to have a value of 1)
    private int calculateScore(List<Card> hand) {
        int total = 0;
        int aceCount = 0;

        for (Card card: hand) {
            int value = card.getValue();
            total += value;
            if (value == 11) {
                aceCount++;
            }
        }
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
    }

    private void dealerTurn() {
        int dealerScore = calculateScore(dealerCardList);

        // Reveals hidden card
        Card hiddenCard = dealerCardList.get(1);
        hiddenCard.setFaceUp(true);
        ImageView hiddenCardView = (ImageView) dealerHand.getChildAt(1);
        hiddenCardView.setImageResource(hiddenCard.getCardID());
        updateScores(true);
        dealerHit();
    }

    // Dealer continues to hit until it is at least 17 or is a bust
    // Adds a small delay between each hit
    private void dealerHit() {
        if (calculateScore(dealerCardList) < 17) {
            drawCard(dealerHand, deck.draw());
            updateScores(true);
            new android.os.Handler().postDelayed(this::dealerHit, 500);
        }
        else {
            determineWinner();
        }
    }

    private void determineWinner() {
        int playerScore = calculateScore(playerCardList);
        int dealerScore = calculateScore(dealerCardList);

        String title;
        String message;

        if (playerScore > 21) {
            title = "You Lost";
            message = "You busted";
        } else if (dealerScore > 21) {
            title = "You Won";
            message = "The dealer busted";
        } else if (playerScore > dealerScore) {
            title = "You Won";
            message = "You have the higher score";
        } else if (dealerScore > playerScore) {
            title = "You Lost";
            message = "The dealer has a higher score.";
        } else {
            title = "Draw";
            message = "Both you and the dealer have the same score";
        }

        new android.os.Handler().postDelayed(() -> {
            // Pass both the title and the message now
            showResults(title, message);
        }, 1500);
    }

    // Win/Lose popup
    private void showResults(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Play Again", (dialog, which) -> {
            resetGame();
        });
        builder.setNegativeButton("Main Menu", (dialog, which) -> {
            finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetGame() {
        playerHand.removeAllViews();
        dealerHand.removeAllViews();

        playerCardList.clear();
        dealerCardList.clear();

        btnHit.setEnabled(true);
        btnStand.setEnabled(true);

        deck = new Deck(this);
        dealHands();
        updateScores(false);
    }

    //updates the scores shown for the player and dealer
    private void updateScores(boolean revealDealer) {
        int playerScore = calculateScore(playerCardList);
        playerScoreText.setText("Player: " + playerScore);

        if (revealDealer) {
            int dealerScore = calculateScore(dealerCardList);
            dealerScoreText.setText("Dealer: " + dealerScore);
        } else {
            // Only show first dealer card
            if (dealerCardList.size() > 0) {
                int visibleValue = dealerCardList.get(0).getValue();
                dealerScoreText.setText("Dealer: " + visibleValue + " + ?");
            }
        }
    }
}