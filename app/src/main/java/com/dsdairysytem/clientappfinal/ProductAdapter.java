package com.dsdairysytem.clientappfinal;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<Map> product_list;


    public ProductAdapter(ArrayList<Map> product_list) {
        this.product_list = product_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        return new ProductAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Map<String,Object> map = (Map<String, Object>) product_list.get(position);
        final String product_name = (String) map.get("Name");
        final String product_quality = (String) map.get("Quality");
        final long product_price = (long) map.get("Price");

        holder.tvProductPrice.setText(Long.toString(product_price));
        holder.tvProductName.setText(product_name);
        holder.tvProductQuality.setText(product_quality);

    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName,tvProductPrice,tvProductQuality;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuality = itemView.findViewById(R.id.tvProductQuality);
        }
    }
}
