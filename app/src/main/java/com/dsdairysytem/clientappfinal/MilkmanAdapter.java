package com.dsdairysytem.clientappfinal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MilkmanAdapter extends RecyclerView.Adapter<MilkmanAdapter.ViewHolder> {

    private ArrayList<String> names , numbers;
    //private String user_id = "+919879874562";
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ProgressDialog progressDialog;

    public MilkmanAdapter(ArrayList<String> names, ArrayList<String> numbers) {
        this.names = names;
        this.numbers = numbers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.milkman_layout, parent, false);
        return new MilkmanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.milkmanName.setText(names.get(position));
        holder.milkmanNumber.setText(numbers.get(position));

        holder.fabDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),MilkmanDetailActivity.class);
                intent.putExtra("milkman_number",numbers.get(position));
                v.getContext().startActivity(intent);
            }
        });

//        holder.fabDisconnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(numbers.get(position))
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()){
//                                    DocumentSnapshot document = task.getResult();
//
//                                    if (document.getLong("Due Amount") == null || document.getLong("Due Amount")==0l){
//
//                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                switch (which){
//                                                    case DialogInterface.BUTTON_POSITIVE:
//                                                        //Yes button clicked
//                                                        progressDialog = new ProgressDialog(v.getContext());
//                                                        progressDialog.setTitle("Delete Product");
//                                                        progressDialog.setMessage("Deleting the Product... Please wait");
//                                                        progressDialog.setCanceledOnTouchOutside(false);
//                                                        progressDialog.show();
//
//                                                        FirebaseFirestore.getInstance().collection("Client").document(user_id).collection("DeliveryPerson").document(numbers.get(position))
//                                                              .delete()
//                                                              .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                  @Override
//                                                                  public void onComplete(@NonNull Task<Void> task) {
//                                                                 if (task.isSuccessful()){
//                                                                     Intent intent = new Intent(v.getContext(),ProfileFragment.class);
//                                                                     intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                                     progressDialog.dismiss();
//                                                                     Toast.makeText(v.getContext(),"Milkman Disconnected",Toast.LENGTH_SHORT).show();
//                                                                     v.getContext().startActivity(intent);
//                                                                 }
//                                                                  }
//                                                              });
//                                                        break;
//
//                                                    case DialogInterface.BUTTON_NEGATIVE:
//                                                        //No button clicked
//
//                                                        break;
//                                                }
//
//
//                                            }
//                                        };
//                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
//                                        builder.setMessage("Are you sure to disconnect ?").setPositiveButton("Yes", dialogClickListener)
//                                                .setNegativeButton("No", dialogClickListener).show();
//
//
//
//                                    }else Toast.makeText(v.getContext(),"Some Amount is still due, clear the Due Amount before Disconnecting",Toast.LENGTH_LONG).show();
//                                }
//
//                            }
//                        });
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView milkmanName, milkmanNumber;
        //ExtendedFloatingActionButton fabDetail, fabDisconnect;
        ImageView fabDetail;

        public ViewHolder(View itemView) {
            super(itemView);

            milkmanName = itemView.findViewById(R.id.inMilkmanName);
            milkmanNumber = itemView.findViewById(R.id.inMikmanNumber);
            fabDetail = itemView.findViewById(R.id.fabDetails);
            //fabDisconnect = itemView.findViewById(R.id.fabDisconnect);

            milkmanNumber.setEnabled(false);
            milkmanName.setEnabled(false);

        }
    }

}
