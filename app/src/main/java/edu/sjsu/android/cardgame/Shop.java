package edu.sjsu.android.cardgame;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Shop extends AppCompatActivity {
    String[] skinNames = {"Classic", "Blue", "Green"};
    String[] skinIDs = {"classic", "blue", "green"};
    int[] skinColors = {
            Color.TRANSPARENT,
            Color.parseColor("#880000FF"),
            Color.parseColor("#8800FF00")
    };
    int[] skinPrices = {0, 0, 0};
    int currentIndex = 0;

    SharedPreferences prefs;
    String equippedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        updateUI();

        findViewById(R.id.btn_next_skin).setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % skinNames.length;
            updateUI();
        });

        findViewById(R.id.btn_prev_skin).setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + skinNames.length) % skinNames.length;
            updateUI();
        });

        findViewById(R.id.btn_select).setOnClickListener(v -> handleActionButton());
        findViewById(R.id.btn_back_menu).setOnClickListener(v -> finish());
    }

    private void updateUI() {
        String themeID = skinIDs[currentIndex];
        equippedTheme = prefs.getString("current_theme", "classic");
        boolean isOwned = prefs.getBoolean("owned_" + themeID, themeID.equals("classic"));

        ((TextView)findViewById(R.id.skin_name)).setText(skinNames[currentIndex]);
        int resID = getResources().getIdentifier(themeID + "_card_base", "drawable", getPackageName());
        ((ImageView)findViewById(R.id.skin_preview)).setImageResource(resID);

        ImageView preview = findViewById(R.id.skin_preview);
        preview.setImageResource(R.drawable.classic_card_base);

        if (themeID.equals("classic")) {
            preview.clearColorFilter();
        }
        else {
            preview.setColorFilter(skinColors[currentIndex], PorterDuff.Mode.SRC_ATOP);
        }

        Button btnAction = findViewById(R.id.btn_select);
        if (themeID.equals(equippedTheme)) {
            btnAction.setText("EQUIPPED");
            btnAction.setEnabled(false);
        }
        else if (isOwned) {
            btnAction.setText("SELECT");
            btnAction.setEnabled(true);
        }
        else {
            btnAction.setText("BUY (" + skinPrices[currentIndex] + ")");
            btnAction.setEnabled(true);
        }

        int coins = prefs.getInt("coins", 100);
        ((TextView)findViewById(R.id.coins_text)).setText("Coins: " + coins);
    }

    private void handleActionButton() {
        String themeID = skinIDs[currentIndex];
        int coins = prefs.getInt("coins", 100);
        int price = skinPrices[currentIndex];
        boolean isOwned = prefs.getBoolean("owned_" + themeID, themeID.equals("classic"));

        SharedPreferences.Editor editor = prefs.edit();

        if (!isOwned) {
            if (coins >= price) {
                coins -= price;
                editor.putInt("coins", coins);
                editor.putBoolean("owned_" + themeID, true); // Mark as owned!

                editor.putString("current_theme", themeID);
                editor.putInt("current_theme_color", skinColors[currentIndex]);
                editor.apply();
            }
        } else {
            editor.putString("current_theme", themeID);
            editor.putInt("current_theme_color", skinColors[currentIndex]);
            editor.apply();
        }
        updateUI();
    }
}