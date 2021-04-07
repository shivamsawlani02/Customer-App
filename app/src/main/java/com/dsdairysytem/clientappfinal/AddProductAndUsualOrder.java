package com.dsdairysytem.clientappfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductAndUsualOrder extends AppCompatActivity {

    EditText nameE,priceE,quantityE;
    Button add;
    Button finish;
    TextView plName,plPrice,plQuantity;
    ArrayList<String> name,quantity,price;
    FirebaseFirestore db;
    String milkmanName,milkmanMobile;
    String clientMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_and_usual_order);

        attachID();

        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddProductAndUsualOrder.this,AddMilkman.class));
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = nameE.getText().toString();
                String q = quantityE.getText().toString();
                String p = priceE.getText().toString();
                if(n.isEmpty())
                    nameE.setError(getString(R.string.product_name_is_required));
                else if (p.isEmpty())
                    priceE.setError(getString(R.string.product_price_is_required));
                else {
                    name.add(n);
                    if(q.isEmpty())
                    {
                        quantity.add("0");
                        plQuantity.append("-" + "\n");
                    }
                    else {
                        quantity.add(q);
                        plQuantity.append(q + " L \n");
                    }
                    price.add(p);
                    plName.append(n + "\n");
                    plPrice.append("@ " + p + " Rs/L \n");

                    nameE.setText("");
                    priceE.setText("");
                    quantityE.setText("");

                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Map<String,Object>> arrayList = new ArrayList<>();
                if (name.isEmpty())
                    Toast.makeText(AddProductAndUsualOrder.this, R.string.add_atleast_one_product, Toast.LENGTH_SHORT).show();
                else {
                    for(int i = 0;i <name.size();i++)
                    {
                        Map<String,Object> map = new HashMap<>();
                        map.put("Quality",name.get(i));
                        map.put("Price",Long.parseLong(price.get(i)));
                        arrayList.add(map);
                    }

                    SelfLogModel model = new SelfLogModel(milkmanName,milkmanMobile,"SelfLog");
                    DocumentReference documentReference = db.collection("Client").document(clientMobile).collection("DeliveryPerson").document(milkmanMobile);

                    documentReference.set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductAndUsualOrder.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Map<String,Long> usualOrderMap = new HashMap<>();

                    for (int i=0 ; i < name.size() ; i++)
                    {
                        if (!quantity.get(i).equalsIgnoreCase("0"))
                            usualOrderMap.put(name.get(i),Long.valueOf(quantity.get(i)));
                    }

                    documentReference.update("Usual Order",usualOrderMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductAndUsualOrder.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    documentReference.update("Products",arrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddProductAndUsualOrder.this, R.string.selflog_added_successfully, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddProductAndUsualOrder.this,SelfLog.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductAndUsualOrder.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    documentReference.update("Due Amount",0);
                }
            }
        });
    }
    private void attachID() {
        nameE = findViewById(R.id.ed_product_name);
        priceE = findViewById(R.id.ed_product_price);
        quantityE = findViewById(R.id.ed_usual_quantity);
        add = findViewById(R.id.btnl_add);
        finish = findViewById(R.id.btnl_finish);
        plName = findViewById(R.id.pl_name);
        plPrice = findViewById(R.id.pl_price);
        plQuantity = findViewById(R.id.pl_quantity);
        name = new ArrayList<>();
        quantity = new ArrayList<>();
        price = new ArrayList<>();
        plName.setText("");
        plQuantity.setText("");
        plPrice.setText("");
        db = FirebaseFirestore.getInstance();
        milkmanName = getIntent().getStringExtra("name");
        milkmanMobile = getIntent().getStringExtra("mobile");
    }
}