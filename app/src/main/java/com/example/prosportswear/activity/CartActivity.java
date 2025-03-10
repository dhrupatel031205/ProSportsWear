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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.example.prosportswear.activity.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI Components
        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        home = findViewById(R.id.home_button);
        store = findViewById(R.id.store_button);
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);
        logout = findViewById(R.id.logoutBtn3);
        checkout = findViewById(R.id.checkout_button);

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(cartAdapter);

        setupNavigation();
        loadCartItems();

        checkout.setOnClickListener(view -> processCheckout());
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
        finish(); // Finish current activity to avoid stacking
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

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No items in cart!", Toast.LENGTH_SHORT).show();
                        cartAdapter.notifyDataSetChanged(); // Clear UI when empty
                        return;
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem item = document.toObject(CartItem.class);
                        if (item != null) {
                            item.setId(document.getId());
                            cartItems.add(item);
                        }
                    }

                    cartAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to load cart: " + e.getMessage());
                    Toast.makeText(this, "Failed to load cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void processCheckout() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Map<String, Object>> purchasedItems = new ArrayList<>();
                    double totalAmount = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("shoeName", doc.getString("shoeName"));
                        item.put("company", doc.getString("company"));
                        item.put("quantity", doc.getLong("quantity"));
                        item.put("price", doc.getDouble("price"));
                        double itemTotal = doc.getDouble("price") * doc.getLong("quantity");
                        item.put("total", itemTotal);
                        totalAmount += itemTotal;

                        purchasedItems.add(item);
                    }

                    // Create bill data
                    Map<String, Object> bill = new HashMap<>();
                    bill.put("items", purchasedItems);
                    bill.put("totalAmount", totalAmount);
                    bill.put("timestamp", FieldValue.serverTimestamp());

                    // Save bill to Firestore
                    db.collection("users").document(userId).collection("Bills")
                            .add(bill)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("CHECKOUT", "Bill saved with ID: " + documentReference.getId());
                                Toast.makeText(this, "Checkout successful!", Toast.LENGTH_SHORT).show();

                                clearCart(userId);

                                // Navigate to ProfileActivity after checkout
                                Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                                intent.putExtra("billId", documentReference.getId());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CHECKOUT", "Failed to save bill: " + e.getMessage());
                                Toast.makeText(this, "Failed to save bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void clearCart(String userId) {
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            batch.delete(db.collection("users").document(userId)
                                    .collection("cart").document(doc.getId()));
                        }

                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    cartItems.clear();
                                    cartAdapter.notifyDataSetChanged();
                                    Log.d("CLEAR_CART", "Cart cleared");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("CLEAR_CART", "Failed to clear cart: " + e.getMessage());
                                    Toast.makeText(this, "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }
}
