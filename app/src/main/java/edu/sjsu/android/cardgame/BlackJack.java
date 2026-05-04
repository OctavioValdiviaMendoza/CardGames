package edu.sjsu.android.cardgame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

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

    private TextView resultText;
    private Button btnPlayAgain;

    private TextView coinsText;
    private SharedPreferences prefs;
    private int coins;

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
        coinsText = findViewById(R.id.coins_text);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        coins = prefs.getInt("coins", 100);

        updateCoinsText();
        Button btnBack = findViewById(R.id.btn_back_menu);

        btnBack.setOnClickListener(v -> finish());

        Button btnRules = findViewById(R.id.btn_rules);

        btnRules.setOnClickListener(v -> showRules());

        resultText = findViewById(R.id.result_text);
        btnPlayAgain = findViewById(R.id.btn_play_again);

        btnPlayAgain.setOnClickListener(v -> resetGame());

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

        cardView.setAlpha(0f);
        if (hand == playerHand) {
            cardView.setTranslationY(200f);
        } else {
            cardView.setTranslationY(-200f);
        }
        hand.addView(cardView);



        cardView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();


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

        if (title.equals("You Won")) {
            changeCoins(10);
        } else if (title.equals("You Lost")) {
            changeCoins(-10);
        }

        resultText.setText(title + "\n" + message);
        btnPlayAgain.setVisibility(View.VISIBLE);

        btnHit.setEnabled(false);
        btnStand.setEnabled(false);
    }

    private void resetGame() {
        resultText.setText("");
        btnPlayAgain.setVisibility(View.GONE);

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

    private void showRules() {
        new AlertDialog.Builder(this)
                .setTitle("Blackjack Rules")
                .setMessage("Try to get as close to 21 as possible without going over.\n\n" +
                        "Tap Hit to draw another card.\n\n" +
                        "Tap Stand to keep your score.\n\n" +
                        "The dealer draws until reaching 17 or higher.\n\n" +
                        "Highest score wins.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void updateCoinsText() {
        coinsText.setText("Coins: " + coins);
    }

    private void changeCoins(int amount) {
        coins += amount;

        if (coins < 0) {
            coins = 0;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", coins);
        editor.apply();

        updateCoinsText();

        if (amount > 0) {
            animateCoins(Color.GREEN);
        } else if (amount < 0) {
            animateCoins(Color.RED);
        }
    }

    private void animateCoins(int color) {
        coinsText.setTextColor(color);
        coinsText.setScaleX(1f);
        coinsText.setScaleY(1f);

        coinsText.animate()
                .scaleX(1.25f)
                .scaleY(1.25f)
                .setDuration(150)
                .withEndAction(() -> {
                    coinsText.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .withEndAction(() -> coinsText.setTextColor(Color.WHITE))
                            .start();
                })
                .start();
    }
}