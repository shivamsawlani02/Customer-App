package com.dsdairysytem.clientappfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class SelfLog extends AppCompatActivity {

    public static ConstraintLayout selectMilkman, selectMilkType, selectMilkQuantity, showAddedLayout, show_order_summary,addProductLayout;
    public static RecyclerView recyclerView, usual_order_list, name_list, milk_type_list, rvQuantityList;
    public static RecyclerView order_summary_list;
    //private String user_id = "+919879874562";
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private int i = 0;
    private long due_amount = 0, amount = 0l;
    private ArrayList<String> milkman_names = new ArrayList<String>();
    private ArrayList<String> milkman_numbers = new ArrayList<String>();
    private ArrayList<String> product_list = new ArrayList<String>();
    private Map<String, Long> product_detail = new HashMap<>();
    private Map<String, String> milkmanMap = new HashMap<>();
    private TextView tvAddedOrder;
    public static TextView tvOrderAmt, tvSelcetedMilkmanName, tvSelectedMilkmanName2;
    public static Button btNext, btBack;
    private ProgressDialog progressDialog;
    private UsualOrderAdapter usualOrderAdapter;
    private AddOrderItemAdapter addOrderItemAdapter;
    private OrderSummaryAdapter orderSummaryAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
    ImageButton backButton;
    ProgressDialog pd;
    TextView addMilkman,addProduct;
    EditText productNameE,productPriceE;
    Button saveProduct,cancelProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_log);

        SelfLogConstant.setState("SELECT_MILKMAN");

        selectMilkType = findViewById(R.id.select_milk_type_layout);
        selectMilkman = findViewById(R.id.select_milkman_layout);
        selectMilkQuantity = findViewById(R.id.select_milk_quantity);
        showAddedLayout = findViewById(R.id.show_added_layout);
        show_order_summary = findViewById(R.id.order_summary_layout);
        tvSelcetedMilkmanName = findViewById(R.id.tvSelectedMilkmanName);
        tvSelectedMilkmanName2 = findViewById(R.id.tvSelectedMilkmanName2);
        addProductLayout = findViewById(R.id.add_product_layout);

        btNext = findViewById(R.id.btNext);
        btBack = findViewById(R.id.btBack);
        usual_order_list = findViewById(R.id.usual_order_list);
        name_list = findViewById(R.id.name_list);
        milk_type_list = findViewById(R.id.milk_type_list);
        rvQuantityList = findViewById(R.id.quantity_list);
        order_summary_list = findViewById(R.id.order_summary_list);
        name_list.setLayoutManager(staggeredGridLayoutManager);
        usual_order_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        addMilkman = findViewById(R.id.add_new_milkman);
        addProduct = findViewById(R.id.add_new_product);
        productNameE = findViewById(R.id.ed_product_name);
        productPriceE = findViewById(R.id.ed_product_price);
        saveProduct = findViewById(R.id.btnl_save);
        cancelProduct = findViewById(R.id.btnl_cancel);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();


        backButton = findViewById(R.id.imageButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelfLog.this,MainActivity.class));
                finish();
            }
        });

        addMilkman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfLog.this,AddMilkman.class);
                startActivity(intent);
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMilkType.setVisibility(View.GONE);
                addProductLayout.setVisibility(View.VISIBLE);
            }
        });

        cancelProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductLayout.setVisibility(View.GONE);
                selectMilkType.setVisibility(View.VISIBLE);
            }
        });



        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson")
                .whereEqualTo("type", "SelfLog")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                        pd.dismiss();

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            milkman_names.add(document.getString("name"));
                            milkman_numbers.add(document.getString("mobile"));
                            milkmanMap.put(document.getString("name"), document.getString("mobile"));
                        }

                        SelfLogConstant.setAllMilkmanMa(milkmanMap);

                        usualOrderAdapter = new UsualOrderAdapter(milkman_names, milkman_numbers, getApplicationContext());
                        usual_order_list.setAdapter(usualOrderAdapter);
                        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.indicator);
                        recyclerIndicator.attachToRecyclerView(usual_order_list);

                        addOrderItemAdapter = new AddOrderItemAdapter(milkman_names, "SELECT_MILKMAN");
                        name_list.setAdapter(addOrderItemAdapter);

                        btNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (btNext.getText().equals("Save")) {
                                    updateUsualOrder(v);
                                } else {

                                    switch (SelfLogConstant.getState()) {

                                        case "SELECT_MILKMAN":
                                            if (SelfLogConstant.getItemList() != null && SelfLogConstant.getItemList().size() == 1) {

                                                progressDialog = new ProgressDialog(v.getContext());
                                                progressDialog.setTitle("Next");
                                                progressDialog.setMessage("Please wait");
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                    }
                                                }, 500);

                                                SelfLogConstant.setMilkmanName(SelfLogConstant.getItemList().get(0));
                                                SelfLogConstant.setItemList(new ArrayList<String>());
                                                SelfLogConstant.setState("SELECT_MILK_TYPE");
                                                selectMilkType.setVisibility(View.VISIBLE);
                                                selectMilkman.setVisibility(View.GONE);
                                                btNext.setBackgroundResource(R.drawable.background_grey);
                                                setMilkTypes(SelfLogConstant.getMilkmanName());
                                            } else
                                                Toast.makeText(v.getContext(), R.string.select_one_milkman, Toast.LENGTH_SHORT).show();
                                            break;

                                        case "SELECT_MILK_TYPE":
                                            if (SelfLogConstant.getItemList() != null && !SelfLogConstant.getItemList().isEmpty()) {

                                                progressDialog = new ProgressDialog(v.getContext());
                                                progressDialog.setTitle("Next");
                                                progressDialog.setMessage("Please wait");
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                    }
                                                }, 500);

                                                SelfLogConstant.setSelectedMilkType(SelfLogConstant.getItemList());
                                                SelfLogConstant.setItemList(new ArrayList<String>());
                                                SelfLogConstant.setState("ENTER_QUANTITY");
                                                selectMilkType.setVisibility(View.GONE);
                                                selectMilkQuantity.setVisibility(View.VISIBLE);
                                                setMilkQuantity(SelfLogConstant.getMilkmanName());
                                            } else
                                                Toast.makeText(v.getContext(), R.string.select_one_to_proceed, Toast.LENGTH_SHORT).show();
                                            break;

                                        case "ENTER_QUANTITY":
                                            if ((SelfLogConstant.getOrderDetail() != null) && (SelfLogConstant.getSelectedMilkType().size() == SelfLogConstant.getOrderDetail().size())) {
                                                SelfLogConstant.setState("ORDER_SUMMARY");
                                                if (SelfLogConstant.isEditUsualOrder()) {
                                                    btNext.setText(R.string.save);
                                                    btBack.setText(R.string.cancel);
                                                } else {
                                                    btNext.setText(R.string.finish);
                                                    btBack.setText(R.string.add_another);
                                                }
                                                amount = 0l;
                                                for (String s : SelfLogConstant.getOrderDetail().keySet()) {
                                                    amount += ((SelfLogConstant.getOrderDetail().get(s)) * (Constant.getProduct_detail_map().get(s)));
                                                }
                                                SelfLogConstant.setTotalAmount(amount);
                                                selectMilkQuantity.setVisibility(View.GONE);
                                                showAddedLayout.setVisibility(View.GONE);
                                                show_order_summary.setVisibility(View.VISIBLE);
                                                addedNewOrder();
                                            } else
                                                Toast.makeText(v.getContext(), R.string.fill_all_quantities, Toast.LENGTH_SHORT).show();
                                            break;

                                        case "ORDER_SUMMARY":
                                            saveNewOrder(v);
                                            break;
                                    }
                                }
                            }
                        });

                        btBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (btBack.getText().equals("Cancel")) {
                                    progressDialog = new ProgressDialog(v.getContext());
                                    progressDialog.setTitle("Cancel");
                                    progressDialog.setMessage("Please wait");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(SelfLog.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            SelfLogConstant.setEditUsualOrder(false);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                        }
                                    }, 500);

                                } else {
                                    switch (SelfLogConstant.getState()) {

                                        case "ENTER_QUANTITY":
                                            selectMilkQuantity.setVisibility(View.GONE);
                                            selectMilkType.setVisibility(View.VISIBLE);
                                            tvSelcetedMilkmanName = findViewById(R.id.tvSelectedMilkmanName);
                                            tvSelcetedMilkmanName.setText(SelfLogConstant.getMilkmanName());
                                            SelfLogConstant.setState("SELECT_MILK_TYPE");
                                            btBack.setVisibility(View.GONE);
                                            milk_type_list.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
                                            addOrderItemAdapter = new AddOrderItemAdapter(new ArrayList<String>(Constant.getProduct_detail_map().keySet()), "SELECT_MILK_TYPE");
                                            milk_type_list.setAdapter(addOrderItemAdapter);
                                            break;

                                        case "ORDER_SUMMARY":
                                            show_order_summary.setVisibility(View.GONE);
                                            selectMilkman.setVisibility(View.VISIBLE);
                                            showAddedLayout.setVisibility(View.VISIBLE);
                                            tvAddedOrder = findViewById(R.id.tvAddedOrder);
                                            tvAddedOrder.setVisibility(View.VISIBLE);
                                            tvAddedOrder.setText(Integer.toString(AddedOrders.getOrderAmounts().size()));
                                            SelfLogConstant.setState("SELECT_MILKMAN");
                                            btNext.setText(R.string.next);
                                            btBack.setText(R.string.back);
                                            btBack.setVisibility(View.GONE);
                                            name_list.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
                                            addOrderItemAdapter = new AddOrderItemAdapter(milkman_names, "SELECT_MILKMAN");
                                            name_list.setAdapter(addOrderItemAdapter);
                                    }
                                }
                            }
                        });
                    }
                    }
                });
    }

    private void updateUsualOrder(View v) {
        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setTitle("Updating Usual Order");
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 1000);

        String milkmanName = AddedOrders.getOrderMilkmanNames().get(0);
        String milkmanNumber = SelfLogConstant.getAllMilkmanMa().get(milkmanName);
        Map<String, Long> usual_order_details = AddedOrders.getOrderDetails().get(0);

        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkmanNumber)
                .update("Usual Order", usual_order_details)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SelfLog.this, R.string.update_usual_order, Toast.LENGTH_SHORT).show();
                            SelfLogConstant.setEditUsualOrder(false);
                            Intent intent = new Intent(SelfLog.this, SelfLog.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                });

    }

    private void addedNewOrder() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Long> amount = new ArrayList<>();
        ArrayList<Map<String, Long>> mapArrayList = new ArrayList<>();
        if (SelfLogConstant.isEditUsualOrder()) {
            names.add(SelfLogConstant.getMilkmanName());
            amount.add(SelfLogConstant.getTotalAmount());
            mapArrayList.add(SelfLogConstant.getOrderDetail());
            AddedOrders.setOrderMilkmanNames(names);
            AddedOrders.setOrderAmounts(amount);
            AddedOrders.setOrderDetails(mapArrayList);
        } else {
            if (AddedOrders.getOrderMilkmanNames().isEmpty() || AddedOrders.getOrderMilkmanNames() == null) {
                names.add(SelfLogConstant.getMilkmanName());
                amount.add(SelfLogConstant.getTotalAmount());
                mapArrayList.add(SelfLogConstant.getOrderDetail());
                AddedOrders.setOrderMilkmanNames(names);
                AddedOrders.setOrderAmounts(amount);
                AddedOrders.setOrderDetails(mapArrayList);
            } else {
                names = new ArrayList<>(AddedOrders.getOrderMilkmanNames());
                amount = new ArrayList<>(AddedOrders.getOrderAmounts());
                mapArrayList = new ArrayList<>(AddedOrders.getOrderDetails());
                names.add(SelfLogConstant.getMilkmanName());
                amount.add(SelfLogConstant.getTotalAmount());
                mapArrayList.add(SelfLogConstant.getOrderDetail());
                AddedOrders.setOrderMilkmanNames(names);
                AddedOrders.setOrderAmounts(amount);
                AddedOrders.setOrderDetails(mapArrayList);
            }
        }
        order_summary_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        orderSummaryAdapter = new OrderSummaryAdapter(getApplicationContext(), names, amount, mapArrayList);
        order_summary_list.setAdapter(orderSummaryAdapter);
    }

    private void saveNewOrder(View v) {
        final int count = AddedOrders.getOrderAmounts().size();
        final int[] check = {0};
        final Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);

        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setTitle("Self Log");
        progressDialog.setMessage("Updating... Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        for (int i = 0; i < count; i++) {
            String milkmanName = AddedOrders.getOrderMilkmanNames().get(i);
            final String milkmanNumber = SelfLogConstant.getAllMilkmanMa().get(milkmanName);
            final Long total_amount = AddedOrders.getOrderAmounts().get(i);
            final Map<String, Long> order_detail = AddedOrders.getOrderDetails().get(i);
            due_amount = 0l;
            due_amount += total_amount;

            FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkmanNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists() && document.get("Due Amount") != null) {
                            due_amount += (Long) document.get("Due Amount");
                        }

                        final Map<String, Object> data = new HashMap<>();
                        data.put("Date", formattedDate);
                        data.put("Amount", total_amount);
                        data.put("Milk", order_detail);
                        data.put("timestamp", c);

                        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkmanNumber).collection("Orders").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if (task.isSuccessful()) {
                                    FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkmanNumber).update("Due Amount", due_amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                check[0]++;
                                                if (check[0] == count) {
                                                    Toast.makeText(SelfLog.this, R.string.orders_added_to_selflog, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SelfLog.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    progressDialog.dismiss();
                                                    startActivity(intent);
                                                }
                                            }

                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            });
        }

    }

    private void setMilkQuantity(String milkmanName) {
        tvSelectedMilkmanName2 = findViewById(R.id.tvSelectedMilkmanName2);
        tvSelectedMilkmanName2.setText(milkmanName);
        btBack.setVisibility(View.VISIBLE);
        rvQuantityList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addOrderItemAdapter = new AddOrderItemAdapter(SelfLogConstant.getSelectedMilkType(), "ENTER_QUANTITY");
        rvQuantityList.setAdapter(addOrderItemAdapter);
    }

    private void setMilkTypes(final String milkmanName) {
        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkmanMap.get(milkmanName)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    product_detail.clear();
                    DocumentSnapshot document = task.getResult();
                    ArrayList<Map> arrayList = (ArrayList<Map>) document.get("Products");

                    if (arrayList != null) {

                        for (Map<String, Object> map : arrayList) {
                            product_detail.put((String) map.get("Quality"), (Long) map.get("Price"));
                        }
                        Constant.setProduct_detail_map(product_detail);
                    }
                    tvSelcetedMilkmanName = findViewById(R.id.tvSelectedMilkmanName);
                    tvSelcetedMilkmanName.setText(milkmanName);
                    milk_type_list.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
                    addOrderItemAdapter = new AddOrderItemAdapter(new ArrayList<String>(product_detail.keySet()), "SELECT_MILK_TYPE");
                    milk_type_list.setAdapter(addOrderItemAdapter);
                }
            }
        });

        saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = productNameE.getText().toString();
                String p = productPriceE.getText().toString();

                if (n.isEmpty())
                    productNameE.setError(getString(R.string.product_name_is_required));
                else if (p.isEmpty())
                    productPriceE.setError(getString(R.string.product_price_is_required));
                else {
                    progressDialog = new ProgressDialog(SelfLog.this);
                    progressDialog.setTitle("Self Log");
                    progressDialog.setMessage("Updating... Please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    String selfLogMobile = milkmanMap.get(milkmanName);
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(selfLogMobile);

                    Map<String,Object> map = new HashMap<>();
                    map.put("Price",Long.valueOf(p));
                    map.put("Quality",n);
                    documentReference.update("Products", FieldValue.arrayUnion(map))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SelfLog.this, R.string.product_added_successfully, Toast.LENGTH_SHORT).show();
                                    addProductLayout.setVisibility(View.GONE);
                                    selectMilkType.setVisibility(View.VISIBLE);
                                    startActivity(new Intent(SelfLog.this,SelfLog.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(SelfLog.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }
}
