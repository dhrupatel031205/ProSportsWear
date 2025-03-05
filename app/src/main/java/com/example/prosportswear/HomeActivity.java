package com.example.prosportswear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    Button logout, home, store, cart, profile;

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

        // Profile Button Click
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Cart Button Click
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Store Button Click
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, StoreActivity.class);
                startActivity(intent);
            }
        });

        // Logout Button Click
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish(); // Close HomeActivity
            }
        });

    }
}
