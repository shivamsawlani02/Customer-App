package com.dsdairysytem.clientappfinal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsualOrderAdapter extends RecyclerView.Adapter<UsualOrderAdapter.ViewHolder> {

    private ArrayList<String> milkman_names;
    private ArrayList<String> milkman_numbers;
    private Context context;
    //private String user_id = "+919879874562";
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private Map<String,Long> map = new HashMap<>();
    private Map<String,Long> map1 = new HashMap<>();
    private UsualOrderProductAdapter usualOrderProductAdapter;
    private AddOrderItemAdapter addOrderItemAdapter;
    private OrderSummaryAdapter orderSummaryAdapter;
    private long totalAmt;
    private Long amount= 0L;
    private Map<String,Long>  product_detail = new HashMap<>();

    public UsualOrderAdapter(ArrayList<String> milkman_names, ArrayList<String> milkman_numbers, Context context) {
        this.milkman_names = milkman_names;
        this.milkman_numbers = milkman_numbers;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usual_order_layout, parent, false);
        return new UsualOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.tvMilkmanName.setText(milkman_names.get(position));
        product_detail.clear();

        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkman_numbers.get(position))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()){
                                map1 = (Map) document.get("Usual Order");
                                product_detail.clear();
                                ArrayList<Map> arrayList =(ArrayList<Map>) document.get("Products");

                                if (arrayList!=null) {

                                    for (Map<String, Object> m : arrayList) {
                                        product_detail.put((String) m.get("Quality"), (Long) m.get("Price"));
                                    }
                                    amount = 0L;

                                    for (String s : map1.keySet()){
                                        amount += ( (map1.get(s))*(product_detail.get(s)) );
                                    }
                                    Map<String,Long> usualOrderAmount = new HashMap<>();
                                    if (SelfLogConstant.getUsualOrderAmount() == null){
                                        usualOrderAmount.put(milkman_names.get(position),amount);
                                        SelfLogConstant.setUsualOrderAmount(usualOrderAmount);
                                    }else {
                                        usualOrderAmount = (Map<String, Long>) SelfLogConstant.getUsualOrderAmount();
                                        usualOrderAmount.put(milkman_names.get(position),amount);
                                        SelfLogConstant.setUsualOrderAmount(usualOrderAmount);
                                    }

                                    holder.tvOrderAmount.setText(Long.toString(amount));
                                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
                                    holder.usual_order_product_list.setLayoutManager(staggeredGridLayoutManager);
                                    usualOrderProductAdapter = new UsualOrderProductAdapter(map1,context);
                                    holder.usual_order_product_list.setAdapter(usualOrderProductAdapter);

                                }
                            }

                        }

                    }
                });
        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfLogConstant.setState("SELECT_MILK_TYPE");
                SelfLogConstant.setEditUsualOrder(true);
                SelfLog.show_order_summary.setVisibility(View.GONE);
                SelfLog.showAddedLayout.setVisibility(View.GONE);
                SelfLog.selectMilkQuantity.setVisibility(View.GONE);
                SelfLog.selectMilkman.setVisibility(View.GONE);
                SelfLog.selectMilkType.setVisibility(View.VISIBLE);

                FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkman_numbers.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            product_detail.clear();
                            DocumentSnapshot document = task.getResult();
                            ArrayList<Map> arrayList =(ArrayList<Map>) document.get("Products");

                            if (arrayList!=null) {

                                for (Map<String, Object> map : arrayList) {
                                    product_detail.put((String) map.get("Quality"), (Long) map.get("Price"));
                                }
                                Constant.setProduct_detail_map(product_detail);
                            }
                            SelfLog.btBack.setVisibility(View.VISIBLE);
                            SelfLog.btBack.setText("Cancel");
                            SelfLogConstant.setMilkmanName(milkman_names.get(position));
                            SelfLog.tvSelcetedMilkmanName.findViewById(R.id.tvSelectedMilkmanName);
                            SelfLog.tvSelcetedMilkmanName.setText(milkman_names.get(position));
                            SelfLog.milk_type_list.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayout.VERTICAL));
                            addOrderItemAdapter = new AddOrderItemAdapter(new ArrayList<String>(product_detail.keySet()),"SELECT_MILK_TYPE");
                            SelfLog.milk_type_list.setAdapter(addOrderItemAdapter);
                        }
                    }
                });

            }
        });

        holder.btSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SelfLogConstant.isEditUsualOrder()){

                    FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkman_numbers.get(position))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {
                                            map = (Map) document.get("Usual Order");
                                            totalAmt = SelfLogConstant.getUsualOrderAmount().get(milkman_names.get(position));


                                            ArrayList<String> names = new ArrayList<>();
                                            ArrayList<Long> amount = new ArrayList<>();
                                            ArrayList<Map<String, Long>> mapArrayList = new ArrayList<>();
                                            if (AddedOrders.getOrderMilkmanNames().isEmpty() || AddedOrders.getOrderMilkmanNames() == null) {
                                                names.add(milkman_names.get(position));
                                                amount.add(totalAmt);
                                                mapArrayList.add(map);
                                                AddedOrders.setOrderMilkmanNames(names);
                                                AddedOrders.setOrderAmounts(amount);
                                                AddedOrders.setOrderDetails(mapArrayList);
                                            } else {
                                                names = new ArrayList<>(AddedOrders.getOrderMilkmanNames());
                                                amount = new ArrayList<>(AddedOrders.getOrderAmounts());
                                                mapArrayList = new ArrayList<>(AddedOrders.getOrderDetails());
                                                names.add(milkman_names.get(position));
                                                amount.add(totalAmt);
                                                mapArrayList.add(map);
                                                AddedOrders.setOrderMilkmanNames(names);
                                                AddedOrders.setOrderAmounts(amount);
                                                AddedOrders.setOrderDetails(mapArrayList);
                                            }
                                            SelfLog.show_order_summary.setVisibility(View.VISIBLE);
                                            SelfLog.showAddedLayout.setVisibility(View.GONE);
                                            SelfLog.selectMilkQuantity.setVisibility(View.GONE);
                                            SelfLog.selectMilkman.setVisibility(View.GONE);
                                            SelfLog.selectMilkType.setVisibility(View.GONE);

                                            SelfLog.btBack.setVisibility(View.VISIBLE);
                                            SelfLog.btNext.setText(R.string.finish);
                                            SelfLog.btNext.setBackgroundResource(R.drawable.curve_rec_blue);
                                            SelfLog.btBack.setText(R.string.add_another);

                                            SelfLogConstant.setState("ORDER_SUMMARY");

                                            SelfLog.order_summary_list.setLayoutManager(new LinearLayoutManager(context));
                                            orderSummaryAdapter = new OrderSummaryAdapter(context, names, amount, mapArrayList);
                                            SelfLog.order_summary_list.setAdapter(orderSummaryAdapter);

                                        }

                                    }

                                }
                            });
            }
                else {
                    Toast.makeText(context, R.string.first_complete_editing,Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return milkman_names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMilkmanName,tvOrderAmount;
        RecyclerView usual_order_product_list;
        Button btSelect , btEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMilkmanName = itemView.findViewById(R.id.tvUsualOrderMilkmanName);
            tvOrderAmount = itemView.findViewById(R.id.tvUsualOrderAmount);
            usual_order_product_list = itemView.findViewById(R.id.usual_order_product_list);
            btEdit = itemView.findViewById(R.id.btEditUsualOrder);
            btSelect = itemView.findViewById(R.id.btSelectUsualOrder);
            }
    }

}
