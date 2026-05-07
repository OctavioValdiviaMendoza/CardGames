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
    private View playerCardContainer;
    private View dealerCardContainer;
    private TextView resultText;
    private Button drawButton;

    private TextView coinsText;
    private SharedPreferences prefs;
    private int coins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_war);

        playerCardContainer = findViewById(R.id.player_card);
        dealerCardContainer = findViewById(R.id.dealer_card);
        getLayoutInflater().inflate(R.layout.activity_card_view, (android.view.ViewGroup) playerCardContainer, true);
        getLayoutInflater().inflate(R.layout.activity_card_view, (android.view.ViewGroup) dealerCardContainer, true);

        resultText = findViewById(R.id.result_text);
        drawButton = findViewById(R.id.draw_button);
        coinsText = findViewById(R.id.coins_text);

        setupInitialCardBacks();

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        coins = prefs.getInt("coins", 100);

        updateCoinsText();

        if (deck == null) {
            deck = new Deck();
        }
        else {
            deck.reset();
        }

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
            deck.reset();
            playerCard = deck.draw();
            dealerCard = deck.draw();
        }

        // Show cards
        setupCardVisuals(playerCardContainer, playerCard);
        setupCardVisuals(dealerCardContainer, dealerCard);

        //card animations
        playerCardContainer.setTranslationY(500f);
        playerCardContainer.setAlpha(0f);
        playerCardContainer
                .animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400).start();

        dealerCardContainer.setTranslationY(-500f);
        dealerCardContainer.setAlpha(0f);
        dealerCardContainer.animate()
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

    private void setupCardVisuals(View container, Card card) {
        ImageView imgBase = container.findViewById(R.id.img_card_base);
        ImageView imgPips = container.findViewById(R.id.img_card_pips);
        ImageView imgRankTop = container.findViewById(R.id.img_rank_top);
        ImageView imgSuitTop = container.findViewById(R.id.img_suit_top);
        ImageView imgRankBot = container.findViewById(R.id.img_rank_bottom);
        ImageView imgSuitBot = container.findViewById(R.id.img_suit_bottom);

        String theme = "classic";
        String suit = card.getSuit();
        String rank = card.getRankLabel();
        String color = (suit.equals("hearts") || suit.equals("diamonds")) ? "red" : "black";

        imgBase.setImageResource(getResources().getIdentifier(theme + "_card_base", "drawable", getPackageName()));

        int pipId;
        if (theme.equals("classic") && (rank.equals("jack") || rank.equals("queen") || rank.equals("king"))) {
            pipId = getResources().getIdentifier(theme + "_" + rank + "_" + color, "drawable", getPackageName());
        } else {
            pipId = getResources().getIdentifier(theme + "_" + suit + "_" + rank, "drawable", getPackageName());
        }
        imgPips.setImageResource(pipId);

        int rankId = getResources().getIdentifier(theme + "_" + rank + "_corner_" + color, "drawable", getPackageName());
        int suitId = getResources().getIdentifier(theme + "_" + suit + "_corner", "drawable", getPackageName());

        imgRankTop.setImageResource(rankId);
        imgRankBot.setImageResource(rankId);
        imgSuitTop.setImageResource(suitId);
        imgSuitBot.setImageResource(suitId);

        // Ensure visibility
        imgPips.setVisibility(View.VISIBLE);
        imgRankTop.setVisibility(View.VISIBLE);
        imgSuitTop.setVisibility(View.VISIBLE);
    }

    private void setupInitialCardBacks() {
        ImageView playerBase = playerCardContainer.findViewById(R.id.img_card_base);
        ImageView dealerBase = dealerCardContainer.findViewById(R.id.img_card_base);

        if (playerBase != null) playerBase.setImageResource(R.drawable.cardback);
        if (dealerBase != null) dealerBase.setImageResource(R.drawable.cardback);

        // Hide pip/rank layers initially
        playerCardContainer.findViewById(R.id.img_card_pips).setVisibility(View.GONE);
        dealerCardContainer.findViewById(R.id.img_card_pips).setVisibility(View.GONE);
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