package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prosportswear.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private Button logout, home, store, cart, profile;
    private TextView userName, userEmail, billTextView;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ✅ Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // ✅ Initialize UI Components
        logout = findViewById(R.id.logoutBtn2);
        home = findViewById(R.id.home_button);
        store = findViewById(R.id.store_button);
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        billTextView = findViewById(R.id.bill_text_view);

        // ✅ Display User Info
        if (user != null) {
            userEmail.setText(user.getEmail() != null ? user.getEmail() : "Not Logged In");
            userName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Guest");
        } else {
            userEmail.setText("Not Logged In");
            userName.setText("Guest");
        }

        // ✅ Load Bill If Present
        String billId = getIntent().getStringExtra("billId");
        if (!TextUtils.isEmpty(billId)) {
            Log.d("PROFILE", "Received Bill ID: " + billId);
            loadBill(billId);
        } else {
            billTextView.setText("No recent bills found.");
            Log.w("PROFILE", "No Bill ID found in Intent");
        }

        // ✅ Handle Navigation
        home.setOnClickListener(v -> navigateTo(HomeActivity.class));
        store.setOnClickListener(v -> navigateTo(StoreActivity.class));
        cart.setOnClickListener(v -> navigateTo(CartActivity.class));
        profile.setOnClickListener(v -> navigateTo(ProfileActivity.class));

        // ✅ Logout Handling
        logout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void navigateTo(Class<?> activity) {
        startActivity(new Intent(ProfileActivity.this, activity));
        finish();
    }

    private void loadBill(String billId) {
        if (user == null) {
            Log.e("LOAD_BILL", "User is null");
            return;
        }

        String userId = user.getUid();

        db.collection("Users")
                .document(userId)
                .collection("Bills")
                .document(billId)
                .get()
                .addOnSuccessListener(documentSnapshot -> handleBillData(documentSnapshot))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LOAD_BILL", "Failed to load bill: " + e.getMessage());
                });
    }

    private void handleBillData(DocumentSnapshot documentSnapshot) {
        if (!documentSnapshot.exists()) {
            Log.w("LOAD_BILL", "No document found with ID");
            billTextView.setText("No recent bills found.");
            return;
        }

        Log.d("LOAD_BILL", "Document Data: " + documentSnapshot.getData());

        // ✅ Extract Bill Data Safely
        List<Map<String, Object>> items = new ArrayList<>();
        Object itemsObject = documentSnapshot.get("items");
        if (itemsObject instanceof List<?>) {
            for (Object obj : (List<?>) itemsObject) {
                if (obj instanceof Map<?, ?>) {
                    items.add((Map<String, Object>) obj);
                }
            }
        }

        if (items.isEmpty()) {
            billTextView.setText("No recent bills found.");
            return;
        }

        double totalAmount = documentSnapshot.getDouble("totalAmount") != null
                ? documentSnapshot.getDouble("totalAmount")
                : 0.0;

        StringBuilder billDetails = new StringBuilder();

        for (Map<String, Object> item : items) {
            String shoeName = (String) item.getOrDefault("shoeName", "Unknown");
            String company = (String) item.getOrDefault("company", "Unknown");
            long quantity = item.get("quantity") != null ? ((Number) item.get("quantity")).longValue() : 0;
            double price = item.get("price") != null ? ((Number) item.get("price")).doubleValue() : 0.0;
            double total = item.get("total") != null ? ((Number) item.get("total")).doubleValue() : 0.0;

            billDetails.append(shoeName)
                    .append(" - ").append(company)
                    .append("\nQty: ").append(quantity)
                    .append(" | Price: $").append(price)
                    .append(" | Total: $").append(total)
                    .append("\n\n");
        }

        billDetails.append("Final Total: $").append(totalAmount);
        billTextView.setText(billDetails.toString());
    }

    private void saveBillToFirestore(String billId, List<Map<String, Object>> items, double totalAmount) {
        if (user == null || items == null || items.isEmpty()) {
            Toast.makeText(this, "No bill data to save", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        Map<String, Object> billData = new HashMap<>();
        billData.put("items", items);
        billData.put("totalAmount", totalAmount);
        billData.put("timestamp", System.currentTimeMillis());

        db.collection("Users")
                .document(userId)
                .collection("Bills")
                .document(billId)
                .set(billData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("SAVE_BILL", "Bill saved successfully");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("SAVE_BILL", "Failed to save bill: " + e.getMessage());
                });
    }
}
