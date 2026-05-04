package edu.sjsu.android.cardgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class War extends AppCompatActivity {

    private Deck deck;
    private ImageView playerCardView;
    private ImageView dealerCardView;
    private TextView resultText;
    private Button drawButton;

    private TextView coinsText;
    private SharedPreferences prefs;
    private int coins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_war);

        playerCardView = findViewById(R.id.player_card);
        dealerCardView = findViewById(R.id.dealer_card);
        resultText = findViewById(R.id.result_text);
        drawButton = findViewById(R.id.draw_button);
        coinsText = findViewById(R.id.coins_text);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        coins = prefs.getInt("coins", 100);

        updateCoinsText();

        deck = new Deck(this);

        drawButton.setOnClickListener(v -> playRound());
        Button btnBack = findViewById(R.id.btn_back_menu);

        btnBack.setOnClickListener(v -> finish());

        Button btnRules = findViewById(R.id.btn_rules);

        btnRules.setOnClickListener(v -> showRules());
    }

    private void playRound() {
        Card playerCard = deck.draw();
        Card dealerCard = deck.draw();

        if (playerCard == null || dealerCard == null) {
            deck = new Deck(this);
            return;
        }

        // Show cards
        playerCardView.setImageResource(playerCard.getCardID());
        dealerCardView.setImageResource(dealerCard.getCardID());
        //card animations
        playerCardView.setTranslationY(500f);
        playerCardView.setAlpha(0f);

        playerCardView.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .start();

        dealerCardView.setTranslationY(-500f);
        dealerCardView.setAlpha(0f);

        dealerCardView.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .start();

        int playerValue = playerCard.getValue();
        int dealerValue = dealerCard.getValue();

        // Determine winner
        if (playerValue > dealerValue) {
            resultText.setText("You Won!\n+10 coins");
            changeCoins(10);
        } else if (dealerValue > playerValue) {
            resultText.setText("Dealer Won!\n-10 coins");
            changeCoins(-10);
        } else {
            resultText.setText("War! It's a Tie!\nNo coins changed");
        }
    }

    private void showRules() {
        new AlertDialog.Builder(this)
                .setTitle("War Rules")
                .setMessage("Each player draws one card.\n\n" +
                        "The higher card wins the round.\n\n" +
                        "If both cards have the same value, it is a tie.")
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