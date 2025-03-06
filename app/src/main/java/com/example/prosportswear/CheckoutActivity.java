package com.example.prosportswear;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private TextView totalAmount;
    private Button homeButton;
    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItem> checkoutItems;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        totalAmount = findViewById(R.id.total_amount);
        homeButton = findViewById(R.id.home_button_checkout);
        checkoutRecyclerView = findViewById(R.id.checkout_recycler_view);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        checkoutItems = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(checkoutItems);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        loadCheckoutItems();

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(CheckoutActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void loadCheckoutItems() {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    totalPrice = 0.0;
                    checkoutItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem item = document.toObject(CartItem.class);
                        checkoutItems.add(item);
                        totalPrice += item.getPrice() * item.getQuantity();
                    }
                    checkoutAdapter.notifyDataSetChanged();
                    totalAmount.setText("Total Amount: $" + String.format("%.2f", totalPrice));
                })
                .addOnFailureListener(e -> Log.e("CheckoutError", "Error loading checkout items", e));
    }
}
