package com.example.prosportswear;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShoeAdapter extends RecyclerView.Adapter<ShoeAdapter.ShoeViewHolder> {
    private List<Shoe> shoeList;

    public ShoeAdapter(List<Shoe> shoeList) {
        this.shoeList = shoeList;
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
    }

    @Override
    public int getItemCount() {
        return shoeList.size();
    }

    public static class ShoeViewHolder extends RecyclerView.ViewHolder {
        TextView name, company, price, count;

        public ShoeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.shoe_name);
            company = itemView.findViewById(R.id.shoe_company);
            price = itemView.findViewById(R.id.shoe_price);
            count = itemView.findViewById(R.id.shoe_count);
        }
    }
}
