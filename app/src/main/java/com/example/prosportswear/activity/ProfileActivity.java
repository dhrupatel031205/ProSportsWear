package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prosportswear.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    Button logout, home, store, cart, profile;
    TextView userName, userEmail;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize UI Components
        logout = findViewById(R.id.logoutBtn2);
        home = findViewById(R.id.home_button);
        store = findViewById(R.id.store_button);
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);

        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);

        // Check if user is logged in
        if (user != null) {
            userEmail.setText(user.getEmail()); // Get email from Firebase

            // Get display name if available
            if (user.getDisplayName() != null) {
                userName.setText(user.getDisplayName());
            } else {
                userName.setText("No Name Provided");
            }
        } else {
            userName.setText("Guest");
            userEmail.setText("Not Logged In");
        }

        // Navigation Buttons
        home.setOnClickListener(view -> navigateTo(HomeActivity.class));
        store.setOnClickListener(view -> navigateTo(StoreActivity.class));
        cart.setOnClickListener(view -> navigateTo(CartActivity.class));

        // Logout Button
        logout.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish(); // Close ProfileActivity
        });
    }

    private void navigateTo(Class<?> activity) {
        startActivity(new Intent(ProfileActivity.this, activity));
        finish();
    }
}
