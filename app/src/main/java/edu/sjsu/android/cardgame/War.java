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
    private int currentRulePage;

    private TextView coinsText;
    private SharedPreferences prefs;
    private int coins;
    private String currentTheme;
    private int themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_war);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        currentTheme = prefs.getString("current_theme", "classic");
        themeColor = prefs.getInt("current_theme_color", Color.TRANSPARENT);

        playerCardContainer = findViewById(R.id.player_card);
        dealerCardContainer = findViewById(R.id.dealer_card);
        getLayoutInflater().inflate(R.layout.activity_card_view, (android.view.ViewGroup) playerCardContainer, true);
        getLayoutInflater().inflate(R.layout.activity_card_view, (android.view.ViewGroup) dealerCardContainer, true);

        resultText = findViewById(R.id.result_text);
        drawButton = findViewById(R.id.draw_button);
        coinsText = findViewById(R.id.coins_text);

        setupInitialCardBacks();

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

    private void setupCardVisuals(View cardView, Card card) {
        int baseCardID;
        int symbolID;
        int suitID;
        int rankID;

        ImageView imageBaseCard = cardView.findViewById(R.id.img_card_base);
        ImageView imageSymbols = cardView.findViewById(R.id.img_card_pips);
        ImageView imageRankTop = cardView.findViewById(R.id.img_rank_top);
        ImageView imageSuitTop = cardView.findViewById(R.id.img_suit_top);
        ImageView imageRankBottom = cardView.findViewById(R.id.img_rank_bottom);
        ImageView imageSuitBottom = cardView.findViewById(R.id.img_suit_bottom);

        String theme = currentTheme;
        String suit = card.getSuit();
        String rank = card.getRankLabel();

        // Base card
        // Filename: "[theme]_card_base"
        baseCardID = getResources().getIdentifier(theme + "_card_base", "drawable", getPackageName());
        // Classic card base is used if file is not found
        if (baseCardID == 0) {
            baseCardID = getResources().getIdentifier("classic_card_base", "drawable", getPackageName());
        }
        imageBaseCard.setImageResource(baseCardID);

        // Symbols
        // Filename: "[theme]_[suit]_[rank]"
        symbolID = getResources().getIdentifier(theme + "_" + suit + "_" + rank, "drawable", getPackageName());
        // Classic card symbol is used if file is not found
        if (symbolID == 0) {
            symbolID = getResources().getIdentifier("classic_" + suit + "_" + rank, "drawable", getPackageName());
        }
        imageSymbols.setImageResource(symbolID);

        // Ranks (corner)
        // Filename: "[theme]_[rank]_corner"
        rankID = getResources().getIdentifier(theme + "_" + rank + "_corner", "drawable", getPackageName());
        // Classic rank is used if file is not found
        if (rankID == 0) {
            rankID = getResources().getIdentifier("classic_" + rank + "_corner", "drawable", getPackageName());
        }
        imageRankTop.setImageResource(rankID);
        imageRankBottom.setImageResource(rankID);

        // Symbols (corner)
        // Filename: "[theme]_[suit]_corner"
        suitID = getResources().getIdentifier(theme + "_" + suit + "_corner", "drawable", getPackageName());
        // Classic suit is used if file is not found
        if (suitID == 0) {
            suitID = getResources().getIdentifier("classic_" + suit + "_corner", "drawable", getPackageName());
        }
        imageSuitTop.setImageResource(suitID);
        imageSuitBottom.setImageResource(suitID);


        if (theme.equals("classic") && (suit.equals("hearts") || suit.equals("diamonds"))) {
            android.graphics.PorterDuff.Mode mode = android.graphics.PorterDuff.Mode.SRC_ATOP;
            imageSymbols.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
            imageRankTop.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
            imageRankBottom.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
            imageSuitTop.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
            imageSuitBottom.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
        }
        else if (theme.equals("blue") || theme.equals("green")) {
            android.graphics.PorterDuff.Mode mode = android.graphics.PorterDuff.Mode.SRC_ATOP;
            imageSymbols.setColorFilter(themeColor, mode);
            imageRankTop.setColorFilter(themeColor, mode);
            imageRankBottom.setColorFilter(themeColor, mode);
            imageSuitTop.setColorFilter(themeColor, mode);
            imageSuitBottom.setColorFilter(themeColor, mode);
        }
        else {
            imageSymbols.clearColorFilter();
            imageRankTop.clearColorFilter();
            imageRankBottom.clearColorFilter();
            imageSuitTop.clearColorFilter();
            imageSuitBottom.clearColorFilter();
        }

        // Ensure visibility
        imageSymbols.setVisibility(View.VISIBLE);
        imageRankTop.setVisibility(View.VISIBLE);
        imageRankBottom.setVisibility(View.VISIBLE);
    }

    private void setupInitialCardBacks() {
        ImageView playerBase = playerCardContainer.findViewById(R.id.img_card_base);
        ImageView dealerBase = dealerCardContainer.findViewById(R.id.img_card_base);

        if (playerBase != null) {
            playerBase.setImageResource(R.drawable.cardback);
            playerBase.clearColorFilter(); // Ensure no tint
        }
        if (dealerBase != null) {
            dealerBase.setImageResource(R.drawable.cardback);
            dealerBase.clearColorFilter(); // Ensure no tint
        }

        // Hide pip/rank layers initially
        playerCardContainer.findViewById(R.id.img_card_pips).setVisibility(View.GONE);
        dealerCardContainer.findViewById(R.id.img_card_pips).setVisibility(View.GONE);
        playerCardContainer.findViewById(R.id.img_rank_top).setVisibility(View.GONE);
        dealerCardContainer.findViewById(R.id.img_rank_top).setVisibility(View.GONE);}

    private void showRules() {
        String[] titles = {
                "How to Play"
        };
        String[] descriptions = {
                "Each player draws one card." +
                    "\n\nThe higher card wins the round. " +
                    "\n\nIf both cards have the same value, it is a tie."
        };
        int[] images = {
                0,
                R.drawable.blackjack_rules
        };

        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_rules, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView ruleTitle = dialogView.findViewById(R.id.rule_title);
        TextView ruleDesc = dialogView.findViewById(R.id.rule_description);
        ImageView ruleImage = dialogView.findViewById(R.id.rule_image);
        Button btnNext = dialogView.findViewById(R.id.btn_next);
        Button btnPrev = dialogView.findViewById(R.id.btn_prev);
        Button btnClose = dialogView.findViewById(R.id.btn_close);

        currentRulePage = 0;

        Runnable updateUI = () -> {
            ruleTitle.setText(titles[currentRulePage]);

            if (descriptions[currentRulePage].isEmpty()) {
                ruleDesc.setVisibility(View.GONE);
            }
            else {
                ruleDesc.setVisibility(View.VISIBLE);
                ruleDesc.setText(descriptions[currentRulePage]);
            }

            if (images[currentRulePage] == 0) {
                ruleImage.setVisibility(View.GONE);
            }
            else {
                ruleImage.setVisibility(View.VISIBLE);
                ruleImage.setImageResource(images[currentRulePage]);
            }
            btnPrev.setVisibility(currentRulePage == 0 ? View.INVISIBLE : View.VISIBLE);

            if (currentRulePage == 0) {
                btnPrev.setVisibility(View.GONE);
            }
            else {
                btnPrev.setVisibility(View.VISIBLE);
            }

            if (currentRulePage == titles.length - 1) {
                btnNext.setVisibility(View.GONE);
            }
            else {
                btnNext.setVisibility(View.VISIBLE);
            }
        };

        btnNext.setOnClickListener(v -> {
            currentRulePage++;
            updateUI.run();
        });

        btnPrev.setOnClickListener(v -> {
            currentRulePage--;
            updateUI.run();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        updateUI.run();
        dialog.show();
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