package edu.sjsu.android.cardgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class War extends AppCompatActivity {

    private Deck deck;
    private ImageView playerCardView;
    private ImageView dealerCardView;
    private TextView resultText;
    private Button drawButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_war);

        playerCardView = findViewById(R.id.player_card);
        dealerCardView = findViewById(R.id.dealer_card);
        resultText = findViewById(R.id.result_text);
        drawButton = findViewById(R.id.draw_button);

        deck = new Deck(this);

        drawButton.setOnClickListener(v -> playRound());
        Button btnBack = findViewById(R.id.btn_back_menu);

        btnBack.setOnClickListener(v -> finish());
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
            resultText.setText("You Won!");
        } else if (dealerValue > playerValue) {
            resultText.setText("Dealer Won!");
        } else {
            resultText.setText("War! It's a Tie!");
        }
    }
}