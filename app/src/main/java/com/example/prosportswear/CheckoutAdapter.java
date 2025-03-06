package com.example.prosportswear;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {
    private List<CartItem> checkoutItems;

    public CheckoutAdapter(List<CartItem> checkoutItems) {
        this.checkoutItems = checkoutItems;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem item = checkoutItems.get(position);
        holder.itemName.setText(item.getShoeCompany());
        holder.itemPrice.setText("$" + item.getPrice());
    }

    @Override
    public int getItemCount() {
        return checkoutItems.size();
    }

    static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.checkout_item_name);
            itemPrice = itemView.findViewById(R.id.checkout_item_price);
        }
    }
}
