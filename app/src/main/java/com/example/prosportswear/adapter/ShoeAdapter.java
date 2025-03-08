package com.example.prosportswear.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prosportswear.R;
import com.example.prosportswear.modal.Shoe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import coil.ImageLoader;
import coil.request.ImageRequest;

public class ShoeAdapter extends RecyclerView.Adapter<ShoeAdapter.ShoeViewHolder> {
    private List<Shoe> shoeList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ShoeAdapter(List<Shoe> shoeList) {
        this.shoeList = shoeList;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ShoeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoe_item, parent, false);
        return new ShoeViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ShoeViewHolder holder, int position) {
        Shoe shoe = shoeList.get(position);

        // ✅ Set Text Data
        holder.name.setText(shoe.getName());
        holder.company.setText(shoe.getCompanyName());
        holder.price.setText("Price: $ " + shoe.getPrice());
        holder.count.setText("Stock: " + shoe.getCount());

        // ✅ Load Image using Coil
        ImageLoader imageLoader = new ImageLoader.Builder(holder.image.getContext()).build();
        ImageRequest request = new ImageRequest.Builder(holder.image.getContext())
                .data(shoe.getPic())
                .crossfade(true)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground) // Replace with an existing drawable
                .target(holder.image)
                .build();

        imageLoader.enqueue(request);

        holder.addToCartButton.setOnClickListener(v -> {
            String quantityText = holder.quantityInput.getText().toString().trim();
            if (quantityText.isEmpty()) {
                Toast.makeText(v.getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0 || quantity > shoe.getCount()) {
                Toast.makeText(v.getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            shoe.setCount(shoe.getCount() - quantity);
            holder.count.setText("Stock: " + shoe.getCount());

            addToCart(shoe, quantity, v);
        });
    }


    @Override
    public int getItemCount() {
        return shoeList.size();
    }

    public static class ShoeViewHolder extends RecyclerView.ViewHolder {
        TextView name, company, price, count;
        EditText quantityInput;
        Button addToCartButton;
        ImageView image; // ✅ ImageView added

        public ShoeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.shoe_name);
            company = itemView.findViewById(R.id.shoe_company);
            price = itemView.findViewById(R.id.shoe_price);
            count = itemView.findViewById(R.id.shoe_count);
            quantityInput = itemView.findViewById(R.id.quantity_input);
            addToCartButton = itemView.findViewById(R.id.add_to_cart);
            image = itemView.findViewById(R.id.shoe_image); // ✅ Bind ImageView
        }
    }
    private void addToCart(Shoe shoe, int quantity, View view) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(view.getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            Log.e("FirestoreError", "User not logged in");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("shoeId", shoe.getId());
        cartItem.put("shoeName", shoe.getName());
        cartItem.put("shoeCompany", shoe.getCompanyName());
        cartItem.put("price", shoe.getPrice());
        cartItem.put("quantity", quantity);
        cartItem.put("imageUrl", shoe.getPic()); // ✅ Add the image URL

        db.collection("users").document(userId)
                .collection("cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(view.getContext(), "Added to Cart!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        updateStockCount(shoe, quantity, view);
    }

    private void updateStockCount(Shoe shoe, int quantity, View view) {
        if (shoe.getId() == null || shoe.getId().isEmpty()) {
            Toast.makeText(view.getContext(), "Error: Shoe ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Store").document(shoe.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentStock = documentSnapshot.getLong("count");
                        if (currentStock != null && currentStock >= quantity) {
                            db.collection("Store").document(shoe.getId())
                                    .update("count", currentStock - quantity)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(view.getContext(), "Stock updated successfully!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(view.getContext(), "Error updating stock: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(view.getContext(), "Not enough stock available!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), "Shoe not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Error fetching shoe data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
