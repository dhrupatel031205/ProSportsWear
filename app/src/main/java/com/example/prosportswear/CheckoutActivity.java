package com.example.prosportswear;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItem> checkoutItems;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView totalAmountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        checkoutRecyclerView = findViewById(R.id.checkout_recycler_view);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalAmountText = findViewById(R.id.total_amount_text);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        checkoutItems = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(checkoutItems);
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        loadCheckoutItems();
    }
    private void loadCheckoutItems() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }


        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalAmount = 0;
                    checkoutItems.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem item = document.toObject(CartItem.class);
                        checkoutItems.add(item);
                        totalAmount += item.getPrice();
                    }
                    checkoutAdapter.notifyDataSetChanged();
                    totalAmountText.setText("Total: $" + totalAmount);
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }
}
