package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    Button logout, home, store, cart, profile;
    TextView userName, userEmail, billTextView;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // UI Components
        logout = findViewById(R.id.logoutBtn2);
        home = findViewById(R.id.home_button);
        store = findViewById(R.id.store_button);
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        billTextView = findViewById(R.id.bill_text_view);

        // Display user info
        if (user != null) {
            userEmail.setText(user.getEmail());
            userName.setText(user.getDisplayName() != null ? user.getDisplayName() : "No Name Provided");
        } else {
            userName.setText("Guest");
            userEmail.setText("Not Logged In");
        }

        // Load and save the latest bill if available
        String billId = getIntent().getStringExtra("billId");
        if (billId != null) {
            loadBill(billId);
        }

        // Navigation Buttons
        home.setOnClickListener(view -> navigateTo(HomeActivity.class));
        store.setOnClickListener(view -> navigateTo(StoreActivity.class));
        cart.setOnClickListener(view -> navigateTo(CartActivity.class));
        profile.setOnClickListener(view -> navigateTo(ProfileActivity.class));

        // Logout Button
        logout.setOnClickListener(view -> {
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
        if (user == null) return;

        String userId = user.getUid();
        db.collection("Users").document(userId).collection("Bills").document(billId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // ✅ Log fetched data
                        Log.d("LOAD_BILL", "Document Snapshot: " + documentSnapshot.getData());

                        // ✅ Type-safe casting
                        List<Map<String, Object>> items = new ArrayList<>();
                        Object itemsObject = documentSnapshot.get("items");
                        if (itemsObject instanceof List) {
                            List<?> list = (List<?>) itemsObject;
                            for (Object obj : list) {
                                if (obj instanceof Map) {
                                    items.add((Map<String, Object>) obj);
                                }
                            }
                        }

                        if (items.isEmpty()) {
                            billTextView.setText("No recent bills found.");
                            return;
                        }

                        double totalAmount = documentSnapshot.getDouble("totalAmount");

                        StringBuilder billDetails = new StringBuilder();
                        for (Map<String, Object> item : items) {
                            billDetails.append(item.get("shoeName"))
                                    .append(" - ").append(item.get("company"))
                                    .append("\nQty: ").append(item.get("quantity"))
                                    .append(" | Price: $").append(item.get("price"))
                                    .append(" | Total: $").append(item.get("total"))
                                    .append("\n\n");
                        }

                        billDetails.append("Final Total: $").append(totalAmount);
                        billTextView.setText(billDetails.toString());
                    } else {
                        billTextView.setText("No recent bills found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

        // Debugging log
        Log.d("SAVE_BILL", "Bill Data: " + billData.toString());

        db.collection("Users").document(userId).collection("Bills").document(billId)
                .set(billData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Failed to save bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
