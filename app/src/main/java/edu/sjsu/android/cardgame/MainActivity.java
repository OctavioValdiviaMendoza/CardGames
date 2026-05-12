package edu.sjsu.android.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonBlackjack = findViewById(R.id.button_blackjack);
        Button buttonThirteen = findViewById(R.id.button_thirteen);
        Button buttonWar = findViewById(R.id.button_war);
        Button buttonExit = findViewById(R.id.butto_exit);
        Button buttonShop = findViewById(R.id.button_shop);

        new Thread(() -> {
            Deck preloadDeck = new Deck();
        }).start();

        buttonBlackjack.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlackJack.class);
            startActivity(intent);
        });
        buttonThirteen.setOnClickListener(v -> {
            Intent intent = new Intent(this, Thirteen.class);
            startActivity(intent);
        });
        buttonWar.setOnClickListener(v -> {
            Intent intent = new Intent(this, War.class);
            startActivity(intent);
        });
        buttonShop.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            startActivity(intent);
        });
        buttonExit.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
