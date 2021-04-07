package com.dsdairysytem.clientappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;


public class Recomendations extends AppCompatActivity {
    String mob;
    String name;
    TextView nearyou;
    List<Map<String, Object>> milktype;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayList<String> arrayList=new ArrayList<>();
    String clientMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    //clientMobile = +915987456326
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lait);
        final Cardadapter adapter=new Cardadapter(getApplicationContext());

        nearyou=findViewById(R.id.nearyoutext);

        Intent intent=getIntent();

        ArrayList<String> docid=intent.getStringArrayListExtra("docid");

        final HListView listView=findViewById(R.id.list_cards);

        String t=Integer.toString(docid.size());


        listView.setAdapter(adapter);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);


        for (int i=0;i<docid.size();i++){

            if(!docid.get(i).equals(clientMobile)){

                Query query=db.collection("Client").document(docid.get(i)).collection("DeliveryPerson")
                        .whereEqualTo("Type","DeliveryPerson");

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("tagging", "onComplete: getting");

                        if (task.isSuccessful()){
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()){
                                    Log.d("AG", "onComplete: "+arrayList.size());
                                    arrayList.removeAll(arrayList);

                                    mob= document.get("mobile").toString();
                                    name= document.get("name").toString();
                                    Log.d("tagging", "onComplete: getting"+mob+name);

                                    DocumentReference documentReference=db.collection("Delivery").document(mob);
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot documentSnapshot=task.getResult();
                                                if (documentSnapshot.exists()){
                                                    Log.d("entering", "entered: " +documentSnapshot);
                                                     milktype = (List<Map<String, Object>>) documentSnapshot.get("Products");
                                                    int k=milktype.size();

                                                    for(int j=0;j<k;j++){
                                                        arrayList.add((String) milktype.get(j).get("Quality"));
                                                    }


                                                }
                                            }
                                        }
                                    });

                                    adapter.add(new Carddetails(mob,name,arrayList));
                                }
                            }
                        }

                    }
                });

            }

        }
        if(docid.size()==1){
            nearyou.setText("NO DELIVERY PERSON FOUND");
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        docid.clear();

    }
}
