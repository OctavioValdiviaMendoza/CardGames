package edu.sjsu.android.cardgame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;

import java.util.ArrayList;
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
    private int currentRulePage;
    private String currentTheme;
    int themeColor;

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

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        currentTheme = prefs.getString("current_theme", "classic");
        themeColor = prefs.getInt("current_theme_color", Color.TRANSPARENT);

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

        if (deck == null) {
            deck = new Deck();
        }
        else {
            deck.reset();
        }
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
                    dealerTurn();
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
    // drawCard calls on drawCardAtIndex so that the dealer can reveal their card
    private void drawCard(LinearLayout hand, Card card) {
        drawCardAtIndex(hand, card, hand.getChildCount());
    }
    private void drawCardAtIndex(LinearLayout hand, Card card, int index) {
        int baseCardID;
        int symbolID;
        int suitID;
        int rankID;

        View cardView = getLayoutInflater().inflate(R.layout.activity_card_view, hand, false);

        ImageView imageBaseCard = cardView.findViewById(R.id.img_card_base);
        ImageView imageSymbols = cardView.findViewById(R.id.img_card_pips);
        ImageView imageRankTop = cardView.findViewById(R.id.img_rank_top);
        ImageView imageSuitTop = cardView.findViewById(R.id.img_suit_top);
        ImageView imageRankBottom = cardView.findViewById(R.id.img_rank_bottom);
        ImageView imageSuitBottom = cardView.findViewById(R.id.img_suit_bottom);

        String theme = currentTheme;
        String suit = card.getSuit();
        String rank = card.getRankLabel();

        if (card.isFaceUp()) {
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
        }
        else {
            // Face down
            imageBaseCard.setImageResource(R.drawable.cardback);
            imageSymbols.setVisibility(View.GONE);
            imageRankTop.setVisibility(View.GONE);
            imageSuitTop.setVisibility(View.GONE);
            imageRankBottom.setVisibility(View.GONE);
            imageSuitBottom.setVisibility(View.GONE);
        }

        // Color filters
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
            imageBaseCard.clearColorFilter();
        }

        // Layout Params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250, 350);
        if (index > 0) {
            params.setMargins(-100, 0, 0, 0);
        }
        cardView.setLayoutParams(params);

        // Add to UI
        hand.addView(cardView, index);

        // Logic: Only add to lists if this is a NEW draw (not a reveal)
        // We check if the hand already contains the card logic-wise
        if (hand == playerHand && !playerCardList.contains(card)) {
            playerCardList.add(card);
        } else if (hand == dealerHand && !dealerCardList.contains(card)) {
            dealerCardList.add(card);
        }

        // Animation
        if (card.isFaceUp() && index == 1 && hand == dealerHand) {
            // Special "Flip" animation for the dealer reveal
            cardView.setRotationY(-90f);
            cardView.animate().rotationY(0f).setDuration(300).start();
        } else {
            // Standard draw animation
            cardView.setAlpha(0f);
            cardView.setTranslationY(hand == playerHand ? 200f : -200f);
            cardView.animate().alpha(1f).translationY(0f).setDuration(400).start();
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
        new android.os.Handler(getMainLooper()).postDelayed(() -> {
            // Flip the hidden card data
            Card hiddenCard = dealerCardList.get(1);
            hiddenCard.setFaceUp(true);

            // Replace the View at index 1
            dealerHand.removeViewAt(1);
            drawCardAtIndex(dealerHand, hiddenCard, 1);

            updateScores(true);
            new android.os.Handler(getMainLooper()).postDelayed(this::dealerHit, 500);
        }, 500);
    }

    // Dealer continues to hit until it is at least 17 or is a bust
    // Adds a small delay between each hit
    private void dealerHit() {
        if (calculateScore(dealerCardList) < 17 && calculateScore(playerCardList) <= 21) {
            new android.os.Handler(getMainLooper()).postDelayed(() -> {
                Card drawnCard = deck.draw();
                if (drawnCard != null) {
                    drawCard(dealerHand, drawnCard);
                    updateScores(true);
                    dealerHit();
                }
            }, 500);
        }
        else {
            new android.os.Handler(getMainLooper()).postDelayed(this::determineWinner, 500);
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

        if (deck == null) {
            deck = new Deck();
        }
        else {
            deck.reset();
        }
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
        String[] titles = {
                "How to Play",
                "Card Scores"
        };
        String[] descriptions = {
                "Press the DRAW button to get another card. " +
                        "\n\nOnce you are satisfied with your score, press STAND. " +
                        "\n\nThis will end your turn and the dealer will play." +
                        "\n\nThe dealer must continue to draw until their score is at least 17. " +
                        "\n\nThe hand with the highest score, without going over 21, will win.",
                ""
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