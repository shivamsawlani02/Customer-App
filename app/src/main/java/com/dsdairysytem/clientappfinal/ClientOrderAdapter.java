package com.dsdairysytem.clientappfinal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class ClientOrderAdapter extends RecyclerView.Adapter<ClientOrderAdapter.OrdersViewHolder> {

    ArrayList<ClientOrder> arrayList;
    Context context;
    Activity parentActivity;

    public ClientOrderAdapter(ArrayList<ClientOrder> arrayList, Context context, Activity parentActivity) {
        this.arrayList = arrayList;
        this.context = context;
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrdersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_order, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        holder.populate(arrayList.get(position));
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder
    {
        TextView date,quantity;
        LinearLayout linearLayout;
        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            quantity=itemView.findViewById(R.id.quantity);
            linearLayout=itemView.findViewById(R.id.linear_layout);
        }
        void populate(final ClientOrder order)
        {
            date.setText("Date : "+order.getDate());
            Map<String,Object> map;
            map=order.getMap();
            quantity.setText("");
            for(Map.Entry<String,Object> entry : map.entrySet())
            {
                quantity.append("\n"+entry.getKey()+" : "+entry.getValue());
            }

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog orderDialog = new Dialog(parentActivity);
                            orderDialog.setContentView(R.layout.detailed_order_dialog);
                            TextView date = orderDialog.findViewById(R.id.dailog_date);
                            TextView time = orderDialog.findViewById(R.id.dailog_time);
                            TextView milkmanName = orderDialog.findViewById(R.id.dialog_milkman);
                            TextView amount = orderDialog.findViewById(R.id.dialog_amount);
                            TextView type = orderDialog.findViewById(R.id.dialog_quality);
                            final Button raiseQuery = orderDialog.findViewById(R.id.dailog_raise_query);

                            FirebaseFirestore.getInstance().collection("Client").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                    .collection("DeliveryPerson").document(order.getMilkmanMobile()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.getString("type").equalsIgnoreCase("SelfLog"))
                                            {
                                                raiseQuery.setVisibility(View.GONE);
                                            }
                                            else
                                            {
                                                raiseQuery.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });


                            date.setText(order.getDate());
                            time.setText(order.getTime());
                            milkmanName.setText(order.getMilkmanName());
                            Log.d("MILKMAN NAME",order.getMilkmanName());
                            amount.setText(order.getAmount());

                            Map<String,Object> map;
                            map=order.getMap();

                            type.setText("");

                            for(Map.Entry<String,Object> entry : map.entrySet())
                            {
                                type.append("\n"+entry.getKey()+" - "+entry.getValue());
                            }



                            raiseQuery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    orderDialog.dismiss();
                                    Intent query = new Intent(context,SendQuery.class);
                                    query.putExtra("milkmanMobile",order.getMilkmanMobile());
                                    query.putExtra("OrderID",order.getID());
                                    query.putExtra("Date",order.getDate());
                                    query.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(query);

                                }
                            });
                            orderDialog.show();
                        }
                    });

                }
            });

        }
    }
}
