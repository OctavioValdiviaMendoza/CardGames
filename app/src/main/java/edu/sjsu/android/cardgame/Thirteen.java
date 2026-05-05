package edu.sjsu.android.cardgame;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Thirteen extends AppCompatActivity {
    private Deck deck;
    private TextView coinsText;
    private SharedPreferences prefs;
    private int coins;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirteen);

        coinsText = findViewById(R.id.coins_text);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        coins = prefs.getInt("coins", 100);

        updateCoinsText();

        deck = new Deck(this);

        Button btnBack = findViewById(R.id.btn_back_menu);

        btnBack.setOnClickListener(v -> finish());

        Button btnRules = findViewById(R.id.btn_rules);

        btnRules.setOnClickListener(v -> showRules());
    }

    private void showRules() {
        new AlertDialog.Builder(this)
                .setTitle("13 Rules")
                .setMessage("Each player draws 13 cards.\n\n" +
                        "Rank of cards goes 3 4 5 6 7 8 9 10 Jack Queen King Ace 2.\n\n" +
                        "Rank of suits goes Spades Clubs Diamonds Hearts.\n\n" +
                        "The player with the lowest card goes first.\n\n" +
                        "Your goal is to make a combination with either \n\n" +
                        "Single, double, triple, quad, or a sequence.\n\n" +
                        "When a combination is played, the other player must match.\n\n" +
                        "You can choose to pass or play your cards.\n\n" +
                        "You can choose to pass or play your cards.\n\n" +
                        "First player to get rid of all cards wins.")
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
