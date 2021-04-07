package com.dsdairysytem.clientappfinal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class MilkmanDetailActivity extends AppCompatActivity {

    private TextView name, number, due_amount, noProduct;
    private RecyclerView recyclerView;
    private String milkman_number;
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ArrayList<Map> product_list = new ArrayList<>();
    private ProductAdapter productAdapter;
    private ExtendedFloatingActionButton fabDisconnect;
    private long dueAmt = 0;
    private ProgressDialog progressDialog;
    ProgressDialog pd;
    ImageView backButton;
    TextView settleToZero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milkman_detail);

        name = findViewById(R.id.tvMilkmanDetailsName);
        number = findViewById(R.id.tvMilkmanDetailsNumber);
        due_amount = findViewById(R.id.tvMilkmanDetailsDueAmt);
        recyclerView = findViewById(R.id.product_list);
        noProduct = findViewById(R.id.tvNoProductiAvailable);
        fabDisconnect = findViewById(R.id.fabMilkmanDetailsDisconnect);
        milkman_number = getIntent().getStringExtra("milkman_number");
        backButton = findViewById(R.id.back_button);
        settleToZero = findViewById(R.id.settle_to_zero);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MilkmanDetailActivity.this,MainActivity.class);
                intent.putExtra("frag_name","Profile");
                startActivity(intent);
                finish();
            }
        });

        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkman_number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (task.isSuccessful()){
                       pd.dismiss();
                       final DocumentSnapshot documentSnapshot = task.getResult();
                       if (documentSnapshot.exists()){
                           name.setText(documentSnapshot.getString("name"));
                           number.setText(documentSnapshot.getString("mobile"));
                           dueAmt = documentSnapshot.getLong("Due Amount");
                           due_amount.setText(Long.toString(dueAmt));
                           product_list = (ArrayList<Map>) documentSnapshot.get("Products");


                           fabDisconnect.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if (documentSnapshot.getLong("Due Amount") == null || dueAmt==0l){

                                       DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               switch (which){
                                                   case DialogInterface.BUTTON_POSITIVE:
                                                       //Yes button clicked
                                                       progressDialog = new ProgressDialog(getApplicationContext());
                                                       progressDialog.setTitle("Delete Product");
                                                       progressDialog.setMessage("Deleting the Product... Please wait");
                                                       progressDialog.setCanceledOnTouchOutside(false);
                                                       progressDialog.show();

                                                       FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(milkman_number)
                                                               .delete()
                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                       if (task.isSuccessful()){
                                                                           Intent intent = new Intent(MilkmanDetailActivity.this,MainActivity.class);
                                                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                           progressDialog.dismiss();
                                                                           Toast.makeText(MilkmanDetailActivity.this, R.string.milkman_disconnected,Toast.LENGTH_SHORT).show();
                                                                           startActivity(intent);
                                                                       }
                                                                   }
                                                               });
                                                       break;

                                                   case DialogInterface.BUTTON_NEGATIVE:
                                                       //No button clicked

                                                       break;
                                               }


                                           }
                                       };
                                       androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                                       builder.setMessage(R.string.are_you_sure_to_disconnect).setPositiveButton("Yes", dialogClickListener)
                                               .setNegativeButton("No", dialogClickListener).show();



                                   }else Toast.makeText(v.getContext(), R.string.clear_due_amount,Toast.LENGTH_LONG).show();

                               }
                           });


                           if (product_list == null){
                            noProduct.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                           }else{
                               recyclerView.setVisibility(View.VISIBLE);
                               noProduct.setVisibility(View.GONE);
                           productAdapter = new ProductAdapter(product_list);
                           //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                           recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                           recyclerView.setAdapter(productAdapter);
                       }
                       }
                   }
                    }
                });

        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson")
                .document(milkman_number).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("type").equalsIgnoreCase("SelfLog"))
                {
                    settleToZero.setVisibility(View.VISIBLE);
                }
            }
        });

        settleToZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (due_amount.getText().equals("0")) {
                    Toast.makeText(MilkmanDetailActivity.this, R.string.amount_is_already_zero, Toast.LENGTH_SHORT).show();
                } else
                {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MilkmanDetailActivity.this);
                alertDialog.setTitle(R.string.sure_to_settle_to_zero)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson")
                                        .document(milkman_number).update("Due Amount", 0)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MilkmanDetailActivity.this, R.string.amount_updated_to_zero, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MilkmanDetailActivity.this, MainActivity.class);
                                                intent.putExtra("frag_name", "Profile");
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
            }
        });

    }
}
