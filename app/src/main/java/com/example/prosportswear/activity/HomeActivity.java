package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prosportswear.R;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    Button logout, home, store, cart, profile;
    private ViewPager2 carousel;
    private LinearLayout indicatorLayout;
    private ImageView[] indicators;
    private Handler handler = new Handler();
    private final int DELAY = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize buttons
        logout = findViewById(R.id.logoutBtn);
        profile = findViewById(R.id.profile_button);
        cart = findViewById(R.id.cart_button);
        store = findViewById(R.id.store_button);
        home = findViewById(R.id.home_button);

        // Initialize carousel and indicator layout
        carousel = findViewById(R.id.carousel);
//        indicatorLayout = findViewById(R.id.indicator_layout);

        // Sample images (replace with actual drawable resources)


        // Set up adapter for carousel

        // Set up indicators

        // Auto-scroll for carousel

        // Listener to update indicators on page change
        carousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        // Set up button click listeners
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        profile.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        cart.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        });

        store.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, StoreActivity.class));
        });

        logout.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, LogoutActivity.class));
            finish(); // Close HomeActivity after logging out
        });
    }

    // Setup indicators dynamically based on the number of carousel items
    private void setupIndicators(int count) {
        indicators = new ImageView[count];
        indicatorLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(indicators[i], layoutParams);
        }
    }

    // Highlight current indicator
    private void setCurrentIndicator(int index) {
        for (int i = 0; i < indicators.length; i++) {
            if (i == index) {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));
            } else {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
