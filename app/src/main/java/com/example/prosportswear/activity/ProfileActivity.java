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
    private Button logout, home, cart, profile;
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

        // ✅ Display User Info
        if (user != null) {
            userEmail.setText(user.getEmail() != null ? user.getEmail() : "Not Logged In");
            userName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Guest");
            loadAllBills(); // Load all bills after user info is shown
        } else {
            userEmail.setText("Not Logged In");
            userName.setText("Guest");
        }

        // ✅ Navigation Handling
        home.setOnClickListener(v -> navigateTo(HomeActivity.class));
        cart.setOnClickListener(v -> navigateTo(CartActivity.class));
        profile.setOnClickListener(v -> navigateTo(ProfileActivity.class));

        // ✅ Logout Handling
        logout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
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

        String[] headerText = {"Shoe Name", "Qty", "Price", "Total"};
        for (String text : headerText) {
            TextView headerCell = new TextView(this);
            headerCell.setText(text);
            headerCell.setPadding(16, 16, 16, 16);
            headerCell.setGravity(Gravity.CENTER);
            headerCell.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            headerCell.setTextColor(getResources().getColor(android.R.color.white));
            header.addView(headerCell); // ✅ Changed from 'row' to 'header'
        }

        billTable.addView(header);
    }

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

            billTotal += total; // ✅ Add to the bill total

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

        // ✅ Total Label
        TextView totalLabel = new TextView(this);
        totalLabel.setText("Total:");
        totalLabel.setPadding(16, 16, 16, 16);
        totalLabel.setGravity(Gravity.CENTER);
        totalLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        totalLabel.setTextColor(getResources().getColor(android.R.color.white));
        totalRow.addView(totalLabel);

        // ✅ Total Value
        TextView totalValue = new TextView(this);
        totalValue.setText(String.valueOf(billTotal));
        totalValue.setPadding(16, 16, 16, 16);
        totalValue.setGravity(Gravity.CENTER);
        totalValue.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        totalValue.setTextColor(getResources().getColor(android.R.color.white));
        totalRow.addView(totalValue);

        // ✅ Add empty cells to align the total row properly
        for (int i = 0; i < 2; i++) {
            TextView emptyCell = new TextView(this);
            emptyCell.setPadding(16, 16, 16, 16);
            totalRow.addView(emptyCell);
        }

        billTable.addView(totalRow);

        // ✅ Add Space After Each Bill
        TableRow spaceRow = new TableRow(this);
        TextView spaceCell = new TextView(this);
        spaceCell.setText("");
        spaceCell.setHeight(30); // ✅ Space Height
        spaceRow.addView(spaceCell);
        billTable.addView(spaceRow);

        return billTotal; // ✅ Return total of each bill (fixed)
    }


    // ✅ Add Total Row at the End
    private void addTotalAmountRow(double totalAmount) {
        TableRow row = new TableRow(this);

        // ✅ Label Cell
        TextView totalLabel = new TextView(this);
        totalLabel.setText("Total Amount:");
        totalLabel.setPadding(16, 16, 16, 16);
        totalLabel.setGravity(Gravity.CENTER);
        totalLabel.setTextColor(getResources().getColor(android.R.color.black));
        totalLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        totalLabel.setTextSize(16);
        row.addView(totalLabel);

        // ✅ Value Cell
        TextView totalValue = new TextView(this);
        totalValue.setText(String.format("%.2f", totalAmount));
        totalValue.setPadding(16, 16, 16, 16);
        totalValue.setGravity(Gravity.CENTER);
        totalValue.setTextColor(getResources().getColor(android.R.color.black));
        totalValue.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        totalValue.setTextSize(16);
        row.addView(totalValue);

        // ✅ Add padding/margin for better spacing
        row.setPadding(0, 20, 0, 20);

        billTable.addView(row);
    }

    // ✅ Navigate to another activity
    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(ProfileActivity.this, targetActivity);
        startActivity(intent);
    }
}
