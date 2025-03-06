package com.example.prosportswear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {
    Button logout, home, store, cart, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize buttons
        logout = findViewById(R.id.logoutBtn2);
        profile = findViewById(R.id.profile_button);
        cart = findViewById(R.id.cart_button);
        store = findViewById(R.id.store_button);
        home = findViewById(R.id.home_button);

        // Home Button Click
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Store Button Click
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, StoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Cart Button Click (Current Activity - No Action Needed)
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User is already on CartActivity, so do nothing
            }
        });

        // Profile Button Click
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Logout Button Click
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish(); // Close CartActivity
            }
        });
    }
}
