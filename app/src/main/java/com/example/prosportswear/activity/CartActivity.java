package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prosportswear.R;
import com.example.prosportswear.adapter.CartAdapter;
import com.example.prosportswear.modal.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button home, store, cart, profile, logout, checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        home = findViewById(R.id.home_button);
        store = findViewById(R.id.store_button);
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);
        logout = findViewById(R.id.logoutBtn3);
        checkout = findViewById(R.id.checkout_button); // Added checkout button

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        setupNavigation();
        loadCartItems();

        checkout.setOnClickListener(v -> checkoutCart()); // Checkout click listener
    }

    private void setupNavigation() {
        home.setOnClickListener(view -> navigateTo(HomeActivity.class));
        store.setOnClickListener(view -> navigateTo(StoreActivity.class));
        cart.setOnClickListener(view -> navigateTo(CartActivity.class));
        profile.setOnClickListener(view -> navigateTo(ProfileActivity.class));
        logout.setOnClickListener(view -> navigateTo(LogoutActivity.class));
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(CartActivity.this, activityClass);
        startActivity(intent);
        finish();
    }

    private void loadCartItems() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem item = document.toObject(CartItem.class);
                        cartItems.add(item);
                    }
                    cartAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartError", "Error loading cart", e);
                    Toast.makeText(this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                });
    }
    private void checkoutCart() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
    }

}
