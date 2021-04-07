package com.dsdairysytem.clientappfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class UsualOrderProductAdapter extends RecyclerView.Adapter<UsualOrderProductAdapter.ViewHolder> {
    private Map<String,Long> map ;
    private Context context;
    private ArrayList<String> quality = new ArrayList<>();
    private ArrayList<Long> quantity = new ArrayList<>();

    public UsualOrderProductAdapter(Map<String, Long> map, Context context) {
        this.map = map;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usual_product_layout, parent, false);
        return new UsualOrderProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        quality.addAll(map.keySet());
        quantity.addAll(map.values());
        holder.tvProductQuantity.setText(quantity.get(position).toString());
        holder.tvProductName.setText(quality.get(position));
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName,tvProductQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvUsualOrderProductName);
            tvProductQuantity = itemView.findViewById(R.id.tvUsualOrderQuantity);
        }
    }
}
