package edu.sjsu.android.cardgame;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Thirteen extends AppCompatActivity {
    private Deck deck;
    private LinearLayout playerHandLayout;
    private LinearLayout dealerHandLayout;
    private LinearLayout currentPileLayout;
    private Button btnPlay;
    private Button btnPass;
    private Button btnPlayAgain;

    private List<Card> playerHand = new ArrayList<>();
    private List<Card> dealerHand = new ArrayList<>();
    private List<Card> selectedCards = new ArrayList<>();
    private List<Card> currentPile = new ArrayList<>();
    private TextView resultText;
    private TextView playerScoreText;
    private TextView dealerScoreText;
    private TextView coinsText;
    private SharedPreferences prefs;
    private int coins;
    private int currentRulePage;

    private String currentTheme;
    int themeColor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirteen);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        currentTheme = prefs.getString("current_theme", "classic");
        themeColor = prefs.getInt("current_theme_color", Color.TRANSPARENT);

        coinsText = findViewById(R.id.coins_text);
        resultText = findViewById(R.id.result_text);
        playerScoreText = findViewById(R.id.player_score);
        dealerScoreText = findViewById(R.id.dealer_score);

        playerHandLayout = findViewById(R.id.player_hand);
        dealerHandLayout = findViewById(R.id.dealer_hand);
        currentPileLayout = findViewById(R.id.current_pile);

        btnPlay = findViewById(R.id.btn_play);
        btnPass = findViewById(R.id.btn_pass);
        btnPlayAgain = findViewById(R.id.btn_play_again);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        coins = prefs.getInt("coins", 100);
        updateCoinsText();

        deckCreate();

        Button btnBack = findViewById(R.id.btn_back_menu);
        btnBack.setOnClickListener(v -> finish());

        Button btnRules = findViewById(R.id.btn_rules);
        btnRules.setOnClickListener(v -> showRules());

        btnPlayAgain.setOnClickListener(v -> resetGame());
        btnPlay.setOnClickListener(v -> playSelectedCards());
        btnPass.setOnClickListener(v -> passTurn());

        startGame();
    }

    private void startGame() {
        deckCreate();
        playerHand.clear();
        dealerHand.clear();
        selectedCards.clear();
        currentPile.clear();
        currentPileLayout.removeAllViews();
        resultText.setText("Your Turn! Select cards to play.");

        for (int i = 0; i < 13; i++) {
            Card playerCard = deck.draw();
            Card dealerCard = deck.draw();

            if (playerCard != null) {
                playerHand.add(playerCard);
            }
            if (dealerCard != null) {
                dealerCard.setFaceUp(false);
                dealerHand.add(dealerCard);
            }
        }

        sortHand(playerHand);
        sortHand(dealerHand);

        renderHand(playerHandLayout, playerHand, false);
        renderHand(dealerHandLayout, dealerHand, true);

        updateCardCounts();
    }

    private void deckCreate() {
        if (deck == null) {
            deck = new Deck();
        }
        else {
            deck.reset();
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

            imageRankTop.setImageResource(rankID);
            imageRankBottom.setImageResource(rankID);
            imageSuitTop.setImageResource(suitID);
            imageSuitBottom.setImageResource(suitID);

            // Reset visibilities for reused views
            imageSymbols.setVisibility(View.VISIBLE);
            imageRankTop.setVisibility(View.VISIBLE);
            imageSuitTop.setVisibility(View.VISIBLE);
            imageRankBottom.setVisibility(View.VISIBLE);
            imageSuitBottom.setVisibility(View.VISIBLE);

            if (theme.equals("classic") && (suit.equals("hearts") || suit.equals("diamonds"))) {
                android.graphics.PorterDuff.Mode mode = android.graphics.PorterDuff.Mode.SRC_ATOP;
                imageSymbols.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
                imageRankTop.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
                imageRankBottom.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
                imageSuitTop.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
                imageSuitBottom.setColorFilter(android.graphics.Color.parseColor("#980000"), mode);
            }
            else if (theme.equals("blue") || theme.equals("green") ||
                    theme.equals("purple") || theme.equals("orange")) {
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
        }
        else {
            // Cardback state
            imageBaseCard.setImageResource(R.drawable.cardback);
            imageBaseCard.clearColorFilter();
            imageSymbols.setVisibility(View.GONE);
            imageRankTop.setVisibility(View.GONE);
            imageSuitTop.setVisibility(View.GONE);
            imageRankBottom.setVisibility(View.GONE);
            imageSuitBottom.setVisibility(View.GONE);
        }
    }

    private void renderHand(LinearLayout layout, List<Card> hand, boolean isDealer) {
        layout.removeAllViews();

        for (Card card : hand) {
            View cardView = getLayoutInflater().inflate(R.layout.activity_card_view, layout, false);

            setupCardVisuals(cardView, card);

            // Selection Logic for Player
            if (layout == playerHandLayout) {
                cardView.setOnClickListener(v -> {
                    if (selectedCards.contains(card)) {
                        selectedCards.remove(card);
                        cardView.setTranslationY(0f);
                    } else {
                        selectedCards.add(card);
                        cardView.setTranslationY(-50f);
                    }
                });
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 300);
            if (layout.getChildCount() > 0) {
                params.setMargins(-140, 0, 0, 0);
            }
            cardView.setLayoutParams(params);
            layout.addView(cardView);
        }
    }


    private void playSelectedCards() {
        if (selectedCards.isEmpty()) {
            return;
        }
        List<Card> cardsToPlay = new ArrayList<>(selectedCards);
        sortHand(selectedCards);

        if (isValidPlay(selectedCards)) {
            btnPlay.setEnabled(false);

            animateCardsToPile(playerHandLayout, cardsToPlay, true, () -> {
                // 2. Clear the pile (fade out dealer's old cards)
                currentPileLayout.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                    currentPile = new ArrayList<>(cardsToPlay);
                    playerHand.removeAll(cardsToPlay);
                    selectedCards.clear();

                    renderHand(playerHandLayout, playerHand, false);
                    renderHand(currentPileLayout, currentPile, false);
                    currentPileLayout.setAlpha(1f);

                    // 3. Wait 1 second, then fade out player cards and trigger dealer
                    new android.os.Handler().postDelayed(() -> {
                        currentPileLayout.animate().alpha(0f).setDuration(500).withEndAction(() -> {
                            dealerTurn();
                        }).start();
                    }, 1000);
                }).start();
            });
        }
        else {
            Toast.makeText(this, "Invalid Play!", Toast.LENGTH_SHORT).show();
            renderHand(playerHandLayout, playerHand, false);
            selectedCards.clear();
        }
    }

    private void passTurn() {
        currentPile.clear();
        currentPileLayout.removeAllViews();
        resultText.setText("You passed. Dealer's free turn.");
        selectedCards.clear();
        renderHand(playerHandLayout, playerHand, false);
        dealerTurn();
    }

    private boolean isValidPlay(List<Card> play) {
        String playType = getComboType(play);
        if (playType.equals("INVALID")) return false;

        if (currentPile.isEmpty()) return true;

        String pileType = getComboType(currentPile);

        if (!playType.equals(pileType) || play.size() != currentPile.size()) return false;

        Card highestPlay = play.get(play.size() - 1);
        Card highestPile = currentPile.get(currentPile.size() - 1);

        return getThirteenValue(highestPlay) > getThirteenValue(highestPile);
    }

    // Identifies Singles, Pairs, Triples, and Sequences
    private String getComboType(List<Card> cards) {
        int size = cards.size();
        if (size == 1) return "SINGLE";

        boolean allSameRank = true;
        for (int i = 0; i < size - 1; i++) {
            if (getRankValue(cards.get(i)) != getRankValue(cards.get(i+1))) {
                allSameRank = false;
                break;
            }
        }
        if (allSameRank) {
            if (size == 2) return "PAIR";
            if (size == 3) return "TRIPLE";
            if (size == 4) return "QUAD";
        }

        if (size >= 3) {
            boolean isSeq = true;
            for (Card c : cards) {
                if (getRankValue(c) == 15) return "INVALID";
            }
            for (int i = 0; i < size - 1; i++) {
                if (getRankValue(cards.get(i + 1)) - getRankValue(cards.get(i)) != 1) {
                    isSeq = false;
                    break;
                }
            }
            if (isSeq) return "SEQUENCE";
        }

        return "INVALID";
    }

    private void dealerTurn() {
        if (dealerHand.isEmpty()) return;

        List<Card> playToMake = new ArrayList<>();

        if (currentPile.isEmpty()) {
            playToMake.add(dealerHand.get(0));
        }
        else {
            String targetType = getComboType(currentPile);
            int targetSize = currentPile.size();
            int pileValue = getThirteenValue(currentPile.get(currentPile.size() - 1));

            for (int i = 0; i <= dealerHand.size() - targetSize; i++) {
                List<Card> potentialPlay = new ArrayList<>();
                for (int j = i; j < dealerHand.size() && potentialPlay.size() < targetSize; j++) {
                    potentialPlay.add(dealerHand.get(j));
                }

                if (getComboType(potentialPlay).equals(targetType) &&
                        getThirteenValue(potentialPlay.get(potentialPlay.size()-1)) > pileValue) {
                    playToMake.addAll(potentialPlay);
                    break;
                }
            }
        }

        if (!playToMake.isEmpty()) {
            animateCardsToPile(dealerHandLayout, playToMake, false, () -> {
                currentPile = new ArrayList<>(playToMake);
                dealerHand.removeAll(playToMake);

                currentPileLayout.setAlpha(1f);
                currentPileLayout.removeAllViews();
                renderHand(currentPileLayout, currentPile, false);

                renderHand(dealerHandLayout, dealerHand, true);

                resultText.setText("Dealer played " + playToMake.size() + " card(s)");
                updateCardCounts();

                if (dealerHand.isEmpty()) {
                    endGame("Dealer Won! -10 coins");
                } else {
                    btnPlay.setEnabled(true);
                    btnPass.setEnabled(true);
                }
            });
        }
        else {
            currentPile.clear();
            currentPileLayout.removeAllViews();
            renderHand(dealerHandLayout, dealerHand, true);
            resultText.setText("Dealer passed. It's your turn");
            btnPlay.setEnabled(true);
            btnPass.setEnabled(true);
        }
        updateCardCounts();
    }

    private int getThirteenValue(Card c) {
        int rankVal = getRankValue(c);
        int suitVal = 0;
        switch (c.getSuit()) {
            case "spades": suitVal = 0; break;
            case "clubs": suitVal = 1; break;
            case "diamonds": suitVal = 2; break;
            case "hearts": suitVal = 3; break;
        }
        return (rankVal * 10) + suitVal;
    }

    private int getRankValue(Card c) {
        String r = c.getRank();
        switch (r) {
            case "ace": return 14;
            case "2": return 15;
            case "jack": return 11;
            case "queen": return 12;
            case "king": return 13;
            default:
                try {
                    return Integer.parseInt(r);
                }
                catch (Exception e) {
                    return 0;
                }
        }
    }

    private void sortHand(List<Card> hand) {
        Collections.sort(hand, (c1, c2) -> Integer.compare(getThirteenValue(c1), getThirteenValue(c2)));
    }

    private void updateCardCounts() {
        playerScoreText.setText("Cards Left: " + playerHand.size());
        dealerScoreText.setText("Dealer Cards: " + dealerHand.size());
    }

    private void endGame(String winner) {
        btnPlay.setEnabled(false);
        btnPass.setEnabled(false);
        resultText.setText(winner);
        btnPlayAgain.setVisibility(View.VISIBLE);

        if (winner.equals("You Won! +10 coins")) changeCoins(10);
        else changeCoins(-10);
    }

    private void resetGame() {
        btnPlay.setEnabled(true);
        btnPass.setEnabled(true);
        btnPlayAgain.setVisibility(View.GONE);
        startGame();
    }

    private void showRules() {
        String[] titles = {
                "How to Play",
                "Card Order",
                "Playable Cards"
        };
        String[] descriptions = {
                "All players start with 13 cards in their hand. " +
                    "\n\nCards are ranked starting from 3 (Lowest) to 2 (highest). " +
                    "If the ranks are tied, the suites will determine the hand is ranked higher. " +
                    "\n\nCards can be played with a single card (Lowest order), a pair of cards, three of a kind, or in a sequence (Highest order)." +
                    "\n\nIf the current play cannot be beaten, the player must pass their turn to the other player. " +
                    "Once all players have had a turn, the round will end. " +
                    "The next round will begin with the player who played last in the last round." +
                    "The first player to get rid of all their cards will win",
                "RANK ORDER: \n3 < 4 < 5 < 6 < 7 < 8 < 9 < 10 < J < Q < K < A < 2" +
                    "\nSUITE ORDER: \nSpades < Clubs < Diamonds < Hearts",
                "PAIR: 2 cards with the same rank" +
                "\nTHREE OF A KIND: 3 cards with the same rank" +
                "\nSEQUENCE: 3 or more cards that are sequentially ranked"
        };
        int[] images = {
                0,
                R.drawable.thirteen_rules_order,
                R.drawable.thirteen_rules_playable,
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

    private void animateCardsToPile(LinearLayout sourceLayout, List<Card> cardsToAnimate, boolean isPlayer, Runnable onComplete) {
        currentPileLayout.post(() -> {
            int[] pileLocation = new int[2];
            currentPileLayout.getLocationOnScreen(pileLocation);

            int totalToAnimate = cardsToAnimate.size();

            View firstCard = sourceLayout.getChildAt(0);
            int cardWidth = (firstCard != null) ? firstCard.getWidth() : 200;
            int cardHeight = (firstCard != null) ? firstCard.getHeight() : 300;

            int cardSpacing = cardWidth - 140;

            int totalOccupiedWidth = cardWidth + ((totalToAnimate - 1) * cardSpacing);
            float horizontalOffset = (currentPileLayout.getWidth() - totalOccupiedWidth) / 2f;
            float verticalOffset = (currentPileLayout.getHeight() - cardHeight) / 2f;

            int cardsFound = 0;
            for (int i = 0; i < sourceLayout.getChildCount(); i++) {
                View cardView = sourceLayout.getChildAt(i);

                boolean shouldAnimate = isPlayer ? (cardView.getTranslationY() == -50f) : (cardsFound < totalToAnimate);

                if (shouldAnimate) {
                    int cardIndexInPlay = cardsFound;
                    cardsFound++;

                    int[] cardLocation = new int[2];
                    cardView.getLocationOnScreen(cardLocation);

                    float targetX = pileLocation[0] + horizontalOffset + (cardIndexInPlay * cardSpacing);
                    float targetY = pileLocation[1] + verticalOffset;
                    float deltaX = targetX - cardLocation[0];
                    float deltaY = targetY - cardLocation[1];

                    if (!isPlayer) {
                        Card cardData = cardsToAnimate.get(cardIndexInPlay);
                        cardData.setFaceUp(true);
                        setupCardVisuals(cardView, cardData);
                    }

                    final int finalCardsFound = cardsFound;
                    cardView.animate()
                            .translationX(deltaX)
                            .translationY(deltaY)
                            .alpha(isPlayer ? 0.9f : 1.0f)
                            .setDuration(500)
                            .withEndAction(() -> {
                                if (finalCardsFound == totalToAnimate) {
                                    if (onComplete != null) onComplete.run();
                                }
                            })
                            .start();
                }
            }
        });
    }
}
