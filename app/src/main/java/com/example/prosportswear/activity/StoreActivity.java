package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prosportswear.R;
import com.example.prosportswear.adapter.ShoeAdapter;
import com.example.prosportswear.modal.Shoe;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class StoreActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ShoeAdapter shoeAdapter;
    private List<Shoe> shoeList;
    private FirebaseFirestore db;
    private Button btnCart, btnProfile, btnRefresh;
    private ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);  // Make sure XML file is correct

        // Initialize UI components
        recyclerView = findViewById(R.id.shoeRecyclerView);
        btnCart = findViewById(R.id.cart_button);
        btnProfile = findViewById(R.id.profile_button);
        btnRefresh = findViewById(R.id.home_button);
        logoImage = findViewById(R.id.app_logo);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoeList = new ArrayList<>();
        shoeAdapter = new ShoeAdapter(shoeList);
        recyclerView.setAdapter(shoeAdapter);

        // Firestore
        db = FirebaseFirestore.getInstance();

        // Load Data
        loadShoes();

        // Button Listeners
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this,CartActivity.class);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        btnRefresh.setOnClickListener(v -> {

            Intent intent = new Intent(StoreActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadShoes() {
        CollectionReference shoeRef = db.collection("Shoe");
        shoeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                shoeList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Shoe shoe = doc.toObject(Shoe.class);
                    shoeList.add(shoe);
                }
                shoeAdapter.notifyDataSetChanged();
            } else {
                Log.e("Firestore", "Error fetching data", task.getException());
            }
        });
    }
}
