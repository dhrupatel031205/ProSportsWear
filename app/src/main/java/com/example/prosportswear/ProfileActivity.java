package com.example.prosportswear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private FirebaseAuth mAuth;
    Button logout;

    Button home, store, cart, profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout = findViewById(R.id.btn_logout);
        profile = findViewById(R.id.btn_profile);
        cart = findViewById(R.id.btn_cart);
        store = findViewById(R.id.btn_store);
        home = findViewById(R.id.btn_home);

        tvUserName = findViewById(R.id.name);
        tvUserEmail = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();

        // Get current logged-in user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "No Name");
            tvUserEmail.setText(user.getEmail());
        }

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Store Button Click
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, StoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Logout Button Click
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish(); // Close HomeActivity
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Close HomeActivity
            }
        });

    }


}
