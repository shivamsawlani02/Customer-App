package com.dsdairysytem.clientappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.dsdairysytem.clientappfinal.send_notification.APIService;
import com.dsdairysytem.clientappfinal.send_notification.Client;
import com.dsdairysytem.clientappfinal.send_notification.Data;
import com.dsdairysytem.clientappfinal.send_notification.MyResponse;
import com.dsdairysytem.clientappfinal.send_notification.NotificationSender;
import com.dsdairysytem.clientappfinal.send_notification.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SendQuery extends AppCompatActivity {
    EditText description;
    Button send;
    FirebaseFirestore db;
    String clientMobile;

    String milkmanMobile;
    String orderID;
    String date;
    String clientName;
    String device_token;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_query);
        attachID();

        clientMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String des=description.getText().toString();

                db.collection("Client").document(clientMobile).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                clientName=documentSnapshot.getString("Name");

                Query query = new Query(clientName,date,des,orderID,clientMobile);
                db.collection("Delivery").document(milkmanMobile).collection("Notification").document().set(query)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                UpdateToken();
                                sendNotifications(milkmanMobile,des,clientName);
                                //sendNotificationToPatner("You have a new query from"+clientName,des);
                                Toast.makeText(SendQuery.this, R.string.query_raised_successfully, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SendQuery.this,MainActivity.class);
                                intent.putExtra("frag_name","Order");
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendQuery.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendQuery.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    void attachID()
    {
        db= FirebaseFirestore.getInstance();
        description=findViewById(R.id.description);
        send=findViewById(R.id.send);
        milkmanMobile=getIntent().getStringExtra("milkmanMobile");
        orderID=getIntent().getStringExtra("OrderID");
        date=getIntent().getStringExtra("Date");
    }

    public void sendNotifications(final String milkmanMobile, final String description, final String clientName) {

        FirebaseFirestore.getInstance().collection("Delivery").document(milkmanMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                device_token=documentSnapshot.getString("Token id");

                //device_token = "c802AXZrRGOvidE_x9eAsy:APA91bFNTa8N_t1YGXVyWu4z5zzxk9qXkLoNtFAdr1GsHadQhgzMQ77r4qWjqbfPtDOKLZ4pwlXt76yayZ-25yY_Ri7HvUtsFD3tFLLO99pJNZnDBiPWs9HZznhtPgFSuGgjwVI5kNz3";
                Token token= new Token(device_token);
                if (device_token != null) {


                    Data data = new Data("You have a new query from "+clientName, description);
                    apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                    NotificationSender sender = new NotificationSender(data,device_token);
                    apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                int ds = response.body().success;
                                Toast.makeText(SendQuery.this, "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
                                Log.d("TAG","-- "+ds);
                                if (response.body().success != 1) {
                                    Toast.makeText(SendQuery.this, "Failed ", Toast.LENGTH_LONG).show();
                                    //updateUI(view);
                                }
                                //updateUI(view);
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            //updateUI(view);
                        }
                    });
                }
                //else updateUI(view);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SendQuery.this, "Unable to fetch token: "+e, Toast.LENGTH_SHORT).show();
                        //updateUI(view);
                    }
                });

    }
    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseFirestore.getInstance().collection("Client").document(clientMobile).update("Token id",refreshToken);
    }
}