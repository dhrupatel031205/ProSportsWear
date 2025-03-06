package com.example.prosportswear.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prosportswear.modal.CartItem;
import com.example.prosportswear.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private FirebaseFirestore db;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        // Debugging logs to verify Firestore data
        Log.d("CartAdapter", "Shoe Name: " + cartItem.getShoeName());
        Log.d("CartAdapter", "Company Name: " + cartItem.getShoeCompany());

        holder.shoeName.setText(cartItem.getShoeName());
        holder.shoeCompany.setText(cartItem.getShoeCompany());
        holder.price.setText("Price: $ " + cartItem.getPrice());
        holder.quantity.setText("Qty: " + cartItem.getQuantity());

        // Handle Remove Button Click
        holder.removeButton.setOnClickListener(v -> removeItemFromFirestore(cartItem, position));
    }
    private void removeItemFromFirestore(CartItem cartItem, int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String itemId = cartItem.getId();

        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(context, "Error: Item ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(userId).collection("cart").document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    cartItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartItems.size());
                    Toast.makeText(context, "Item removed from cart!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to remove item: " + e.getMessage());
                    Toast.makeText(context, "Failed to remove item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView shoeName, shoeCompany, price, quantity;
        Button removeButton;

        public CartViewHolder(View itemView) {
            super(itemView);
            shoeName = itemView.findViewById(R.id.cart_shoe_name);
            shoeCompany = itemView.findViewById(R.id.cart_shoe_company);
            price = itemView.findViewById(R.id.cart_shoe_price);
            quantity = itemView.findViewById(R.id.cart_quantity);
            removeButton = itemView.findViewById(R.id.remove_from_cart);
        }
    }
}
