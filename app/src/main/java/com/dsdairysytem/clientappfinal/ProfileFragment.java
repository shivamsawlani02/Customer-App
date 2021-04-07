package com.dsdairysytem.clientappfinal;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private TextView name, email, contact, add1, add2, city, firstLetter;
    private RecyclerView recyclerView;
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ArrayList<String> milkman_names = new ArrayList<>();
    private ArrayList<String> milkman_numbers = new ArrayList<>();
    private MilkmanAdapter milkmanAdapter;
    ProgressDialog pd;

    private Boolean intro;
    private SharedPreferences introPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile,container,false);

        name = view.findViewById(R.id.tvProfileName);
        firstLetter = view.findViewById(R.id.tvFirstLetter);
        email = view.findViewById(R.id.tvProfileEmail);
        contact = view.findViewById(R.id.tvProfileMobile);
        add1 = view.findViewById(R.id.tvProfileAdd1);
        add2 = view.findViewById(R.id.tvProfileAdd2);
        city = view.findViewById(R.id.tvProfileCity);
        recyclerView = view.findViewById(R.id.milkman_list);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        introPref = getActivity().getSharedPreferences("MAIN",MODE_PRIVATE);
        intro = introPref.getBoolean("order",true);

        FirebaseFirestore.getInstance().collection("Client").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        pd.dismiss();
                        name.setText(documentSnapshot.getString("Name"));
                        char letter = (documentSnapshot.getString("Name")).charAt(0);
                        firstLetter.setText(Character.toString(letter));
                        email.setText(documentSnapshot.getString("Email"));
                        contact.setText(documentSnapshot.getString("Mobile"));
                        city.setText(documentSnapshot.getString("City"));
                        add1.setText(documentSnapshot.getString("adl1"));
                        add2.setText(documentSnapshot.getString("adl2"));
                    }
                }
            }
        });

        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        pd.dismiss();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            milkman_names.add(documentSnapshot.getString("name"));
                            milkman_numbers.add(documentSnapshot.getString("mobile"));
                        }

                        milkmanAdapter = new MilkmanAdapter(milkman_names,milkman_numbers);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(milkmanAdapter);
                    }
                });

        final TapTargetSequence sequence = new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forView(view.findViewById(R.id.textView18),getString(R.string.connected_milkman),getString(R.string.connected_milkman_tutorial))
                                .outerCircleColor(R.color.green)
                                .outerCircleAlpha(0.75f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(60)
                                .dimColor(R.color.black)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });

        if (intro) {
            sequence.start();
            getActivity().getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("profile",false).apply();
        }


        return view;
    }
}
