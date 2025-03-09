package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prosportswear.R;

public class HomeActivity extends AppCompatActivity {

    Button logout, home, store, cart, profile;
    TextView exploreMore;

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
        exploreMore = findViewById(R.id.explore_more);


        profile.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        });

        cart.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
            finish();
        });

        store.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, StoreActivity.class));
            finish();

        });

        logout.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, LogoutActivity.class));
            finish(); // Close HomeActivity after logging out
        });

        exploreMore.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, StoreActivity.class));
            finish();
        });
    }
}
