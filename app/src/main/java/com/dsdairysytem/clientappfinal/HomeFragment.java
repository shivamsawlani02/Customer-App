package com.dsdairysytem.clientappfinal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    TextView clientFirstName, clientFirstLetter;
    LinearLayout selfLog, connectToMilkman;
    String clientMobile;
    FirebaseFirestore db;
    ProgressDialog pd;
    int flag=1;
    View view;
    String milkManName;
    Long amount;
    Map<String,Object> milk;
    String date;
    TextView amountT,milkT,milkManNameT;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    Button addOrder;
    private Boolean intro;
    private SharedPreferences introPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home, container, false);
        clientMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //clientMobile="+919879874562";
        Log.d("CLIENT MOBILE",clientMobile);

        clientFirstName = view.findViewById(R.id.client_first_name);
        clientFirstLetter = view.findViewById(R.id.client_first_letter);
        milkManNameT = view.findViewById(R.id.last_order_milkman);
        amountT = view.findViewById(R.id.last_order_amount);
        milkT = view.findViewById(R.id.last_order_quantity);
        linearLayout = view.findViewById(R.id.layout_not_connected);
        relativeLayout = view.findViewById(R.id.layout_connected);
        addOrder = view.findViewById(R.id.add_order);
        linearLayout.setVisibility(View.GONE);
        addOrder.setVisibility(View.INVISIBLE);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        introPref = getActivity().getSharedPreferences("MAIN",MODE_PRIVATE);
        intro = introPref.getBoolean("home",true);

        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SelfLog.class));
            }
        });

        db = FirebaseFirestore.getInstance();

        db.collection("Client").document(clientMobile).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString("Name");
                        if (!name.contains(" "))
                            clientFirstName.setText(name);
                        else
                            clientFirstName.setText(name.substring(0, name.indexOf(" ") + 1));
                        Log.d("FIRST NAME", name);
                        clientFirstLetter.setText(name.substring(0, 1));
                    }
                });


        db.collection("Client").document(clientMobile).collection("DeliveryPerson").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if(task.getResult().isEmpty())
                            {
                                notConnected();
                            }
                            else
                                {
                                    //notConnected();
                                    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    date="01/01/1950";
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("Client").document(clientMobile).collection("DeliveryPerson").document(document.getId())
                                            .collection("Orders").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                                            .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful())
                                            {
//                                                if(!task.getResult().isEmpty()) {

                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        Date timestamp = documentSnapshot.getTimestamp("timestamp").toDate();
                                                        try {
                                                            Date time = sdf.parse(date);

                                                            if(timestamp.after(time)) {
                                                                date = sdf.format(timestamp);
                                                                amount = documentSnapshot.getLong("Amount");
                                                                milk = (Map<String, Object>) documentSnapshot.get("Milk");
                                                                milkManName = document.getString("name");

                                                            }
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }


                                                        if (milk != null) {
                                                            connected(milkManName, milk, amount);
                                                        } else {
                                                            linearLayout.setVisibility(View.GONE);
                                                            relativeLayout.setVisibility(View.VISIBLE);
                                                            amountT.setVisibility(View.INVISIBLE);
                                                            //addOrder.setVisibility(View.VISIBLE);
                                                            milkT.setVisibility(View.GONE);
                                                            milkManNameT.setText(R.string.you_have_no_past_order);
                                                            addOrder.setVisibility(View.VISIBLE);
                                                        }


                                            }
                                        }
                                    });
                                }

                            }
                            pd.dismiss();
                        }
                    }
                });

        final TapTargetSequence sequence = new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forView(view.findViewById(R.id.side_nav_button),getString(R.string.menu),getString(R.string.menu_tutorial))
                                .outerCircleColor(R.color.red)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(40)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true),
                        TapTarget.forView(view.findViewById(R.id.tv_connect_to_milkman),getString(R.string.connect_to_milkman),getString(R.string.connect_milkman_tutorial))
                                .outerCircleColor(R.color.orange)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(40)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true) ,
                        TapTarget.forView(view.findViewById(R.id.tv_self_log),getString(R.string.self_log),getString(R.string.self_log_tutorial))
                                .outerCircleColor(R.color.violet)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(40)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true)

                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                });

        if (intro) {
            sequence.start();
            getActivity().getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("home",false).apply();
        }

            return view;

        }
        void notConnected() {

            linearLayout.setVisibility(View.VISIBLE);
            selfLog = view.findViewById(R.id.self_log);
            connectToMilkman = view.findViewById(R.id.connect_to_milkman);


            selfLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //client is taken to the self log activity
                    Intent selfLog = new Intent(getActivity(), AddMilkman.class);
                    startActivity(selfLog);
                }
            });

            connectToMilkman.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent connect = new Intent(getActivity(), QRCodeGenerator.class);
                    startActivity(connect);
                }
            });
        }

        void connected(String milkManName,Map<String,Object> milk,Long amount) {


            linearLayout.setVisibility(View.GONE);
            linearLayout.setVisibility(View.INVISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);
            amountT.setVisibility(View.VISIBLE);
            addOrder.setVisibility(View.VISIBLE);
            //addOrder.setVisibility(View.VISIBLE);
            milkT.setVisibility(View.VISIBLE);


            milkManNameT.setText(getString(R.string.milkman)+" "+milkManName);
            amountT.setText("₹ "+amount);
            milkT.setText("");
            if(milk != null) {
                for(Map.Entry<String,Object> entry : milk.entrySet())
                {
                    milkT.append("\n"+entry.getKey()+" - "+entry.getValue()+"L");
                }
            }

        }

}
