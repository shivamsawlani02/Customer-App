package com.dsdairysytem.clientappfinal;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddOrderItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> list;
    private ArrayList<String> selected_items = new ArrayList<>();
    private Map<String,Long> order_detail = new HashMap<>();
    private String type;

    public AddOrderItemAdapter(ArrayList<String> list, String type) {
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (type.equals("SELECT_MILKMAN")||type.equals("SELECT_MILK_TYPE")){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new AddOrderItemAdapter.ViewHolder1(view);}

        else if (type.equals("ENTER_QUANTITY")){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enter_quantity_layout, parent, false);
            return new AddOrderItemAdapter.ViewHolder2(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usual_product_layout, parent, false);
            return new AddOrderItemAdapter.ViewHolder3(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        SelfLog.btNext.findViewById(R.id.btNext);

        if (type.equals("SELECT_MILKMAN")||type.equals("SELECT_MILK_TYPE")){
            ((ViewHolder1)holder).tvItem.setText(list.get(position));
            ((ViewHolder1)holder).tvItem.setHint("NOT SELECTED");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ViewHolder1)holder).tvItem.getHint().equals("NOT SELECTED")){
                        ((ViewHolder1)holder).tvItem.setBackgroundResource(R.drawable.curve_rec_blue);
                        ((ViewHolder1)holder).tvItem.setTextColor(Color.parseColor("#ffffff"));
                        ((ViewHolder1)holder).tvItem.setHint("SELECTED");
                        ((ViewHolder1)holder).tvItem.setText(list.get(position));
                        selected_items.add(list.get(position));
                        SelfLogConstant.setItemList(selected_items);
                        SelfLog.btNext.setBackgroundResource(R.drawable.curve_rec_blue);
                    }else {
                        ((ViewHolder1)holder).tvItem.setTextColor(Color.parseColor("#000000"));
                        ((ViewHolder1)holder).tvItem.setBackgroundResource(R.drawable.curve_rec_background);
                        ((ViewHolder1)holder).tvItem.setHint("NOT SELECTED");
                        ((ViewHolder1)holder).tvItem.setText(list.get(position));
                        selected_items.remove(list.get(position));
                        SelfLogConstant.setItemList(selected_items);
                        if (selected_items.isEmpty()){
                            SelfLog.btNext.setBackgroundResource(R.drawable.background_grey);
                        }
                    }
                }
            });
        }

        else if (type.equals("ENTER_QUANTITY")){
            ((ViewHolder2)holder).tvProductName.setText(list.get(position));
            ((ViewHolder2)holder).tvPrice.setText(Long.toString(Constant.getProduct_detail_map().get(list.get(position))));
            ((ViewHolder2)holder).etQuantity.addTextChangedListener(new TextWatcher() {

                private String ss;
                private long after;
                private Thread t;
                private Runnable runnable_EditTextWatcher = new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if ((System.currentTimeMillis() - after) > 200)
                            {
                                Log.d("Debug_EditTEXT_watcher", "(System.currentTimeMillis()-after)>600 ->  " + (System.currentTimeMillis() - after) + " > " + ss);
                                // Do your stuff
                                if (!ss.equals("0")&&!ss.equals("")){
                                    if (order_detail.get(list.get(position)) != null ){
                                        order_detail.remove(list.get(position));
                                        order_detail.put(list.get(position),Long.parseLong(ss));
                                        SelfLogConstant.setOrderDetail(order_detail);
                                    } else {
                                        order_detail.put(list.get(position),Long.parseLong(ss));
                                        SelfLogConstant.setOrderDetail(order_detail);
                                    }

                                }
                                t = null;
                                break;
                            }
                        }
                    }
                };


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ss = s.toString();
                    after = System.currentTimeMillis();
                    if (t == null)
                    {
                        t = new Thread(runnable_EditTextWatcher);
                        t.start();
                    }
                }
            });
        } else {
            ((ViewHolder3)holder).tvProductName.setText(list.get(position));
            ((ViewHolder3)holder).tvProductQuantity.setText(Long.toString(SelfLogConstant.getOrderDetail().get(list.get(position))));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder {

        TextView tvItem;

        public ViewHolder1(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tvItem);
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView tvProductName, tvPrice;
        EditText etQuantity;

        public ViewHolder2(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvEnterQuantityMilkType);
            tvPrice = itemView.findViewById(R.id.tvEnterQuantityPrice);
            etQuantity = itemView.findViewById(R.id.etEnterQuantity);
        }
    }

    public static class ViewHolder3 extends RecyclerView.ViewHolder {

        TextView tvProductName,tvProductQuantity;

        public ViewHolder3(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvUsualOrderProductName);
            tvProductQuantity = itemView.findViewById(R.id.tvUsualOrderQuantity);
        }
    }


}
