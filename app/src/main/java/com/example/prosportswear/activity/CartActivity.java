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

        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        home = findViewById(R.id.home_button);
        store = findViewById(R.id.store_button);
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);
        logout = findViewById(R.id.logoutBtn3);
        checkout = findViewById(R.id.checkout_button);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
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
        finish();
    }
    private void loadCartItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No items in cart!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    cartItems.clear();
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
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Map<String, Object>> purchasedItems = new ArrayList<>();
                        double totalAmount = 0;

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
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

                        Map<String, Object> bill = new HashMap<>();
                        bill.put("items", purchasedItems);
                        bill.put("totalAmount", totalAmount);
                        bill.put("timestamp", FieldValue.serverTimestamp());

                        db.collection("users").document(userId).collection("Bills")
                                .add(bill)
                                .addOnSuccessListener(documentReference -> {
                                    clearCart(userId);
                                    Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                                    intent.putExtra("billId", documentReference.getId());
                                    startActivity(intent);
                                    finish();
                                });
                    } else {
                        Toast.makeText(CartActivity.this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearCart(String userId) {
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        db.collection("users").document(userId).collection("cart")
                                .document(doc.getId()).delete();
                    }
                });
    }
}