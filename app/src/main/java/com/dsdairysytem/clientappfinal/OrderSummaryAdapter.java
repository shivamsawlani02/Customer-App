package com.dsdairysytem.clientappfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Map;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {
    private Context context;
    private  ArrayList<String> names;
    private ArrayList<Long> amount;
    private ArrayList<Map<String,Long>> mapArrayList;
    private UsualOrderProductAdapter usualOrderProductAdapter;

    public OrderSummaryAdapter(Context context, ArrayList<String> names, ArrayList<Long> amount, ArrayList<Map<String, Long>> mapArrayList) {
        this.context = context;
        this.names = names;
        this.amount = amount;
        this.mapArrayList = mapArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary_item_layout, parent, false);
        return new OrderSummaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvOrderSummaryAmount.setText(amount.get(position).toString());
        holder.tvOrderSummaryMilkman.setText(names.get(position));
        holder.order_summary_list.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        usualOrderProductAdapter = new UsualOrderProductAdapter(mapArrayList.get(position),context);
        holder.order_summary_list.setAdapter(usualOrderProductAdapter);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderSummaryMilkman, tvOrderSummaryAmount;
        RecyclerView order_summary_list;

        public ViewHolder(View itemView) {
            super(itemView);
            tvOrderSummaryMilkman = itemView.findViewById(R.id.tvOrderSummaryMilkman);
            tvOrderSummaryAmount = itemView.findViewById(R.id.tvOrderSummaryAmount);
            order_summary_list = itemView.findViewById(R.id.order_summary_list);
        }
    }

}
