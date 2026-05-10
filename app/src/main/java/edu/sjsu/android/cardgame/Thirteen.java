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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirteen);

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
        ImageView imgBase = cardView.findViewById(R.id.img_card_base);
        ImageView imgPips = cardView.findViewById(R.id.img_card_pips);
        ImageView imgRankTop = cardView.findViewById(R.id.img_rank_top);
        ImageView imgSuitTop = cardView.findViewById(R.id.img_suit_top);
        ImageView imgRankBot = cardView.findViewById(R.id.img_rank_bottom);
        ImageView imgSuitBot = cardView.findViewById(R.id.img_suit_bottom);

        String theme = "classic";
        String suit = card.getSuit();
        String rank = card.getRankLabel();
        String color = (suit.equals("hearts") || suit.equals("diamonds")) ? "red" : "black";

        if (card.isFaceUp()) {
            imgBase.setImageResource(getResources().getIdentifier(theme + "_card_base", "drawable", getPackageName()));

            // Pips vs Face Card Logic
            int pipId;
            if (theme.equals("classic") && (rank.equals("jack") || rank.equals("queen") || rank.equals("king"))) {
                pipId = getResources().getIdentifier(theme + "_" + rank + "_" + color, "drawable", getPackageName());
            } else {
                pipId = getResources().getIdentifier(theme + "_" + suit + "_" + rank, "drawable", getPackageName());
            }
            imgPips.setImageResource(pipId);

            // Corners
            int rankId = getResources().getIdentifier(theme + "_" + rank + "_corner_" + color, "drawable", getPackageName());
            int suitId = getResources().getIdentifier(theme + "_" + suit + "_corner", "drawable", getPackageName());

            imgRankTop.setImageResource(rankId);
            imgRankBot.setImageResource(rankId);
            imgSuitTop.setImageResource(suitId);
            imgSuitBot.setImageResource(suitId);

            // Reset visibilities for reused views
            imgPips.setVisibility(View.VISIBLE);
            imgRankTop.setVisibility(View.VISIBLE);
            imgSuitTop.setVisibility(View.VISIBLE);
        } else {
            // Cardback state
            imgBase.setImageResource(R.drawable.cardback);
            imgPips.setVisibility(View.GONE);
            imgRankTop.setVisibility(View.GONE);
            imgSuitTop.setVisibility(View.GONE);
            imgRankBot.setVisibility(View.GONE);
            imgSuitBot.setVisibility(View.GONE);
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

        /**for (Card card : hand) {
            ImageView cardView = new ImageView(this);

            if (isDealer && layout == dealerHandLayout) {
                cardView.setImageResource(R.drawable.cardback);
            } else {
                cardView.setImageResource(card.getCardID());

                // Only allow clicking if it's in the player's hand
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
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 300);
            if (layout.getChildCount() > 0) {
                params.setMargins(-140, 0, 0, 0);
            }
            cardView.setLayoutParams(params);
            layout.addView(cardView);
        }*/
    }


    private void playSelectedCards() {
        if (selectedCards.isEmpty()) return;
        sortHand(selectedCards);

        if (isValidPlay(selectedCards)) {
            currentPile = new ArrayList<>(selectedCards);
            playerHand.removeAll(selectedCards);
            selectedCards.clear();

            renderHand(playerHandLayout, playerHand, false);

            for(Card c : currentPile) c.setFaceUp(true);
            renderHand(currentPileLayout, currentPile, false);

            updateCardCounts();

            if (playerHand.isEmpty()) {
                endGame("You Won! +10 coins");
            } else {
                dealerTurn();
            }
        } else {
            Toast.makeText(this, "Invalid Play!", Toast.LENGTH_SHORT).show();
            selectedCards.clear();
            renderHand(playerHandLayout, playerHand, false);
        }
    }
    /**private void playSelectedCards() {
        if (selectedCards.isEmpty()) {
            return;
        }

        sortHand(selectedCards);

        if (isValidPlay(selectedCards)) {
            currentPile = new ArrayList<>(selectedCards);
            playerHand.removeAll(selectedCards);
            selectedCards.clear();

            renderHand(playerHandLayout, playerHand, false);
            renderHand(currentPileLayout, currentPile, false);
            updateCardCounts();

            if (playerHand.isEmpty()) {
                endGame("You Won! +10 coins");
            } else {
                dealerTurn();
            }
        } else {
            Toast.makeText(this, "Invalid Play! Matches pile type/size and beat highest card.", Toast.LENGTH_SHORT).show();
            selectedCards.clear();
            renderHand(playerHandLayout, playerHand, false);
        }
    }*/

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

        // Must match combo type and size (3-card sequence beats 3-card sequence)
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

        // Check for Sequence (Straight)
        if (size >= 3) {
            boolean isSeq = true;
            for (Card c : cards) {
                if (getRankValue(c) == 15) return "INVALID"; // 2s cannot be in a sequence
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
            playToMake.add(dealerHand.get(0)); // Dealer plays lowest card on free turn
        } else {
            String targetType = getComboType(currentPile);
            int targetSize = currentPile.size();
            int pileValue = getThirteenValue(currentPile.get(currentPile.size() - 1));

            // Basic Dealer AI: Tries to find a combination that matches size and beats the value
            // (Uses a brute-force combination check to keep logic contained)
            for (int i = 0; i <= dealerHand.size() - targetSize; i++) {
                List<Card> potentialPlay = new ArrayList<>();
                for (int j = i; j < dealerHand.size() && potentialPlay.size() < targetSize; j++) {
                    potentialPlay.add(dealerHand.get(j));
                }

                // If it doesn't match perfectly sequentially, we try jumping over cards
                // If it's a valid play, dealer makes it.
                if (getComboType(potentialPlay).equals(targetType) &&
                        getThirteenValue(potentialPlay.get(potentialPlay.size()-1)) > pileValue) {
                    playToMake.addAll(potentialPlay);
                    break;
                }
            }
        }

        if (!playToMake.isEmpty()) {
            currentPile = new ArrayList<>(playToMake);
            dealerHand.removeAll(playToMake);
            resultText.setText("Dealer played " + playToMake.size() + " card(s)");
            renderHand(currentPileLayout, currentPile, false); // Show dealer's play in center!

            if (dealerHand.isEmpty()) {
                endGame("Dealer Won! -10 coins");
            }
        } else {
            currentPile.clear();
            currentPileLayout.removeAllViews();
            resultText.setText("Dealer passed. Your free turn.");
        }

        renderHand(dealerHandLayout, dealerHand, true);
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
}
