package com.example.prosportswear.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prosportswear.modal.CartItem;
import com.example.prosportswear.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
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
        holder.shoeName.setText(cartItem.getShoeName());
        holder.shoeCompany.setText(cartItem.getShoeCompany());
        holder.price.setText("Price: $ " + cartItem.getPrice());
        holder.quantity.setText("Qty: " + cartItem.getQuantity());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView shoeName, shoeCompany, price, quantity;

        public CartViewHolder(View itemView) {
            super(itemView);
            shoeName = itemView.findViewById(R.id.cart_shoe_name);
            shoeCompany = itemView.findViewById(R.id.cart_shoe_company);
            price = itemView.findViewById(R.id.cart_shoe_price);
            quantity = itemView.findViewById(R.id.cart_quantity);
        }
    }
}
