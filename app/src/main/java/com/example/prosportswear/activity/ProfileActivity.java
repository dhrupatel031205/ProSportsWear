package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prosportswear.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private Button logout, home, cart, profile, store;
    private TextView userName, userEmail;
    private TableLayout billTable;
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
        cart = findViewById(R.id.cart_button);
        profile = findViewById(R.id.profile_button);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        billTable = findViewById(R.id.bill_table);
        store = findViewById(R.id.store_button);

        // ✅ Display User Info
        if (user != null) {
            userEmail.setText(user.getEmail() != null ? user.getEmail() : "Not Logged In");
            userName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Guest");
            loadAllBills(); // Load all bills after user info is shown
        } else {
            userEmail.setText("Not Logged In");
            userName.setText("Guest");
        }

        // ✅ Intent-Based Navigation
        home.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // ✅ Close current activity
        });

        cart.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
            startActivity(intent);
            finish(); // ✅ Close current activity
        });
        store.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, StoreActivity.class);
            startActivity(intent);
            finish(); // ✅ Close current activity
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // ✅ Close current activity
        });

        // ✅ Logout Handling
        logout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LogoutActivity.class));
            finish();
        });
    }

    // ✅ Load All Bills From Firestore
    private void loadAllBills() {
        if (user == null) {
            Log.e("LOAD_BILLS", "User is null");
            return;
        }

        String userId = user.getUid();

        db.collection("users")
                .document(userId)
                .collection("Bills")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    clearTable(); // ✅ Clear previous table data
                    setupTableHeader(); // ✅ Set up table header

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        addBillToTable(document.getData());
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load bills: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LOAD_BILLS", "Failed to load bills: " + e.getMessage());
                });
    }

    // ✅ Clear Existing Table Data
    private void clearTable() {
        billTable.removeAllViews();
    }

    // ✅ Create Table Header
    private void setupTableHeader() {
        TableRow header = new TableRow(this);

        String[] headerText = {"Item Name", "Qty", "Price", "Total"};
        for (String text : headerText) {
            TextView headerCell = new TextView(this);
            headerCell.setText(text);
            headerCell.setPadding(16, 16, 16, 16);
            headerCell.setGravity(Gravity.CENTER);
            headerCell.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            headerCell.setTextColor(getResources().getColor(android.R.color.white));
            header.addView(headerCell);
        }

        billTable.addView(header);
    }

    // ✅ Add Bill to Table and Calculate Total
    private double addBillToTable(Map<String, Object> data) {
        List<Map<String, Object>> items = new ArrayList<>();
        Object itemsObject = data.get("items");
        if (itemsObject instanceof List<?>) {
            for (Object obj : (List<?>) itemsObject) {
                if (obj instanceof Map<?, ?>) {
                    items.add((Map<String, Object>) obj);
                }
            }
        }

        double billTotal = 0.0; // ✅ Total for each bill

        for (Map<String, Object> item : items) {
            String shoeName = (String) item.getOrDefault("shoeName", "Unknown");
            long quantity = item.get("quantity") != null ? ((Number) item.get("quantity")).longValue() : 0;
            double price = item.get("price") != null ? ((Number) item.get("price")).doubleValue() : 0.0;
            double total = item.get("total") != null ? ((Number) item.get("total")).doubleValue() : 0.0;

            billTotal += total;

            TableRow row = new TableRow(this);
            String[] rowData = {shoeName, String.valueOf(quantity), String.valueOf(price), String.valueOf(total)};
            for (String cellData : rowData) {
                TextView cell = new TextView(this);
                cell.setText(cellData);
                cell.setPadding(16, 16, 16, 16);
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundColor(getResources().getColor(android.R.color.white));
                cell.setTextColor(getResources().getColor(android.R.color.black));
                row.addView(cell);
            }
            billTable.addView(row);
        }

        // ✅ Add Total Row for Each Individual Bill
        TableRow totalRow = new TableRow(this);

        // ✅ Total Label (span 3 columns)
        TextView totalLabel = new TextView(this);
        totalLabel.setText("Total:");
        totalLabel.setPadding(16, 16, 16, 16);
        totalLabel.setGravity(Gravity.CENTER);
        totalLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        totalLabel.setTextColor(getResources().getColor(android.R.color.white));
        TableRow.LayoutParams totalLabelParams = new TableRow.LayoutParams();
        totalLabelParams.span = 3;
        totalLabel.setLayoutParams(totalLabelParams);
        totalRow.addView(totalLabel);

        // ✅ Total Value
        TextView totalValue = new TextView(this);
        totalValue.setText(String.valueOf(billTotal));
        totalValue.setPadding(16, 16, 16, 16);
        totalValue.setGravity(Gravity.CENTER);
        totalValue.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        totalValue.setTextColor(getResources().getColor(android.R.color.white));
        totalRow.addView(totalValue);

        billTable.addView(totalRow);

        return billTotal;
    }
}
