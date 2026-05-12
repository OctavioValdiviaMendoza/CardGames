package edu.sjsu.android.cardgame;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Shop extends AppCompatActivity {
    String[] skinNames = {
            "Classic",
            "Blue",
            "Green",
            "Orange",
            "Purple",
            "Deluxe"
    };
    String[] skinIDs = {
            "classic",
            "blue",
            "green",
            "orange",
            "purple",
            "deluxe"
    };
    int[] skinColors = {
            Color.TRANSPARENT,
            Color.parseColor("#880000FF"),
            Color.parseColor("#8800FF00"),
            Color.parseColor("#88FF8800"),
            Color.parseColor("#88550088"),
            Color.TRANSPARENT
    };
    int[] skinPrices = {0, 200, 200, 200, 200, 1000};
    int currentIndex = 0;

    SharedPreferences prefs;
    String equippedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        updateUI();

        findViewById(R.id.button_next_skin).setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % skinNames.length;
            updateUI();
        });

        findViewById(R.id.button_prev_skin).setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + skinNames.length) % skinNames.length;
            updateUI();
        });

        findViewById(R.id.button_select).setOnClickListener(v -> handleActionButton());
        findViewById(R.id.button_back_menu).setOnClickListener(v -> finish());
    }

    private void updateUI() {
        String themeID = skinIDs[currentIndex];
        equippedTheme = prefs.getString("current_theme", "classic");
        boolean isOwned = prefs.getBoolean("owned_" + themeID, themeID.equals("classic"));

        ((TextView)findViewById(R.id.skin_name)).setText(skinNames[currentIndex]);

        android.widget.FrameLayout cardContainer = findViewById(R.id.skin_preview);
        cardContainer.removeAllViews();
        View cardView = getLayoutInflater().inflate(R.layout.activity_card_view, cardContainer, true);

        ImageView imageBaseCard = cardView.findViewById(R.id.image_card_base);
        ImageView imageSymbols = cardView.findViewById(R.id.image_card_symbols);
        ImageView imageRankTop = cardView.findViewById(R.id.image_rank_top);
        ImageView imageSuitTop = cardView.findViewById(R.id.image_suit_top);
        ImageView imageRankBottom = cardView.findViewById(R.id.image_rank_bottom);
        ImageView imageSuitBottom = cardView.findViewById(R.id.image_suit_bottom);

        int baseID = getResources().getIdentifier(themeID + "_card_base", "drawable", getPackageName());
        if (baseID == 0) {
            baseID = R.drawable.classic_card_base;
        }
        imageBaseCard.setImageResource(baseID);

        int symbolID = getResources().getIdentifier(themeID + "_spades_ace", "drawable", getPackageName());
        if (symbolID == 0) {
            symbolID = R.drawable.classic_spades_ace;
        }
        imageSymbols.setImageResource(symbolID);


        int rankID = getResources().getIdentifier(themeID + "_ace_corner", "drawable", getPackageName());
        if (rankID == 0) {
            rankID = R.drawable.classic_ace_corner;
        }
        imageRankTop.setImageResource(rankID);
        imageRankBottom.setImageResource(rankID);

        int suitID = getResources().getIdentifier(themeID + "_spades_corner", "drawable", getPackageName());
        if (suitID == 0) suitID = R.drawable.classic_spades_corner;
        imageSuitTop.setImageResource(suitID);
        imageSuitBottom.setImageResource(suitID);

        imageSymbols.setVisibility(View.VISIBLE);
        imageRankTop.setVisibility(View.VISIBLE);
        imageSuitTop.setVisibility(View.VISIBLE);
        imageRankBottom.setVisibility(View.VISIBLE);
        imageSuitBottom.setVisibility(View.VISIBLE);

        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        if (themeID.equals("blue") || themeID.equals("green") ||
                themeID.equals("purple") || themeID.equals("orange")) {
            int color = skinColors[currentIndex];
            imageSymbols.setColorFilter(color, mode);
            imageRankTop.setColorFilter(color, mode);
            imageRankBottom.setColorFilter(color, mode);
            imageSuitTop.setColorFilter(color, mode);
            imageSuitBottom.setColorFilter(color, mode);
        }
        else {
            imageSymbols.clearColorFilter();
            imageRankTop.clearColorFilter();
            imageRankBottom.clearColorFilter();
            imageSuitTop.clearColorFilter();
            imageSuitBottom.clearColorFilter();
            imageBaseCard.clearColorFilter();
        }

        Button btnAction = findViewById(R.id.button_select);
        if (themeID.equals(equippedTheme)) {
            btnAction.setText(R.string.equipped);
            btnAction.setEnabled(false);
        }
        else if (isOwned) {
            btnAction.setText(R.string.select);
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