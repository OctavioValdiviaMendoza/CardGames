package edu.sjsu.android.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnBlackjack;
    private Button btnThirteen;
    private Button btnWar;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBlackjack = findViewById(R.id.btnBlackjack);
        btnThirteen = findViewById(R.id.btnThirteen);
        btnWar = findViewById(R.id.btnWar);
        btnExit = findViewById(R.id.btnExit);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Thread(() -> {
            Deck preloadDeck = new Deck();
        }).start();

        btnBlackjack.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlackJack.class);
            startActivity(intent);
        });
        btnThirteen.setOnClickListener(v -> {
            Intent intent = new Intent(this, Thirteen.class);
            startActivity(intent);
        });
        btnWar.setOnClickListener(v -> {
            Intent intent = new Intent(this, War.class);
            startActivity(intent);
        });
        btnExit.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
