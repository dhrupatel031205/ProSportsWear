package com.example.prosportswear;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void onBindViewHolder(@NonNull ShoeViewHolder holder, int position) {
        Shoe shoe = shoeList.get(position);
        holder.name.setText(shoe.getName());
        holder.company.setText(shoe.getCompanyName());
        holder.price.setText("Price: $ " + shoe.getPrice());
        holder.count.setText("Stock: " + shoe.getCount());

        holder.addToCartButton.setOnClickListener(v -> {
            String quantityText = holder.quantityInput.getText().toString().trim();
            if (quantityText.isEmpty()) {
                Toast.makeText(v.getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                Toast.makeText(v.getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            addToCart(shoe, quantity, v);
        });
    }

    @Override
    public int getItemCount() {
        return shoeList.size();
    }

    public class ShoeViewHolder extends RecyclerView.ViewHolder {
        TextView name, company, price, count;
        EditText quantityInput;
        Button addToCartButton;

        public ShoeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.shoe_name);
            company = itemView.findViewById(R.id.shoe_company);
            price = itemView.findViewById(R.id.shoe_price);
            count = itemView.findViewById(R.id.shoe_count);
            quantityInput = itemView.findViewById(R.id.quantity_input);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }

    private void addToCart(Shoe shoe, int quantity, View view) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(view.getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("shoeName", shoe.getName());
        cartItem.put("shoeCompany", shoe.getCompanyName());
        cartItem.put("price", shoe.getPrice());
        cartItem.put("quantity", quantity);

        db.collection("users").document(userId)
                .collection("cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(view.getContext(), "Added to Cart!", Toast.LENGTH_SHORT).show();
                    Log.d("FirestoreSuccess", "Item added: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Failed to add: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("FirestoreError", "Error adding to cart", e);
                });
    }
}
