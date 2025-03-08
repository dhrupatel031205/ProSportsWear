package com.example.prosportswear.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prosportswear.R;
import com.example.prosportswear.modal.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private FirebaseFirestore db;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.shoeName.setText(item.getShoeName());
        holder.shoeCompany.setText(item.getCompany());
        holder.shoePrice.setText("$" + item.getPrice());
        holder.shoeQuantity.setText("Quantity: " + item.getQuantity());

        // ✅ Load image using Coil
        ImageLoader imageLoader = new ImageLoader.Builder(context).build();
        ImageRequest request = new ImageRequest.Builder(context)
                .data(item.getImageUrl()) // URL of the image
                .crossfade(true) // Smooth transition
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image
                .error(R.drawable.ic_launcher_foreground) // Error image
                .target(holder.shoeImage) // Target ImageView
                .build();

        imageLoader.enqueue(request);

        holder.removeButton.setOnClickListener(v -> removeItem(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private void removeItem(int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String id = cartItems.get(position).getId();

        db.collection("users")
                .document(userId)
                .collection("cart")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    cartItems.remove(position);
                    notifyItemRemoved(position);
                });
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView shoeName, shoeCompany, shoePrice, shoeQuantity;
        Button removeButton;
        ImageView shoeImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            shoeName = itemView.findViewById(R.id.shoe_name);
            shoeCompany = itemView.findViewById(R.id.shoe_company);
            shoePrice = itemView.findViewById(R.id.shoe_price);
            shoeQuantity = itemView.findViewById(R.id.shoe_quantity);
            removeButton = itemView.findViewById(R.id.remove_button);
            shoeImage = itemView.findViewById(R.id.shoe_image);
        }
    }
}
