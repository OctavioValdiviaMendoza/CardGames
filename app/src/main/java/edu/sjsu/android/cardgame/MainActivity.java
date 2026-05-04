package edu.sjsu.android.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnBlackjack;
    private Button btnSeventeen;
    private Button btnWar;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBlackjack = findViewById(R.id.btnBlackjack);
        btnSeventeen = findViewById(R.id.btnSeventeen);
        btnWar = findViewById(R.id.btnWar);
        btnExit = findViewById(R.id.btnExit);

        btnBlackjack.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlackJack.class);
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
}
