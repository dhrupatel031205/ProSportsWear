package com.example.prosportswear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StoreActivity extends AppCompatActivity {
    Button home, cart, profile, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // Initialize buttons
        home = findViewById(R.id.home_button);
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);
        logout = findViewById(R.id.logoutBtn2);

        // Home Button Navigation
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Close StoreActivity
            }
        });

        // Cart Button Navigation
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Profile Button Navigation
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Logout Button Navigation
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
