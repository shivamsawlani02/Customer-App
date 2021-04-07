package com.dsdairysytem.clientappfinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.datatransport.BuildConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class OrderFragment extends Fragment {

    RecyclerView recyclerView;
    ClientOrderAdapter adapter;
    ArrayList<ClientOrder> arrayList;
    FirebaseFirestore db;
    Spinner spinner;
    ProgressBar pgBar;
    String clientMobile;
    ArrayAdapter<String> arrayAdapter, mobileArrayAdapter;
    int c = 0;
    CalendarView calendarView;
    NestedScrollView nestedScrollView;
    ImageView sideNavButton;
    ProgressDialog pd;
    TextView notConnected;
    Button btPdf;
    ImageView btSharePdf;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    Dialog customDialog;
    private Button btExPdf, btExcel;
    private ImageView btCancel;
    private Boolean intro;
    private SharedPreferences introPref;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_orders,container,false);
        attachID(view);
        clientMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //clientMobile = "+919879874562";

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        introPref = getActivity().getSharedPreferences("MAIN",MODE_PRIVATE);
        intro = introPref.getBoolean("order",true);

        sideNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
                if(!mDrawerLayout.isDrawerOpen(GravityCompat.START))
                mDrawerLayout.openDrawer(GravityCompat.START);
                else
                    mDrawerLayout.closeDrawer(GravityCompat.END);

            }
        });

        db.collection("Client").document(clientMobile).collection("DeliveryPerson").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mobileArrayAdapter.add(document.getId());
                                arrayAdapter.add(document.getString("name"));
                            }
                            if (arrayAdapter.getCount() == 0) {
                                spinner.setVisibility(View.INVISIBLE);
                                recyclerView.setVisibility(View.INVISIBLE);
                                btPdf.setVisibility(View.INVISIBLE);
                                btSharePdf.setVisibility(View.INVISIBLE);
                                notConnected.setVisibility(View.VISIBLE);
                            } else
                            {
                                spinner.setAdapter(arrayAdapter);

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                                    pd.show();
                                    //pagination(mobileArrayAdapter.getItem(i), arrayAdapter.getItem(i),"");
                                    fetchOrders(mobileArrayAdapter.getItem(i), arrayAdapter.getItem(i), "");
                                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                        @Override
                                        public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                                            pd.show();
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                            String date;

                                            if (("" + month).startsWith("1"))
                                                date = dayOfMonth + "/" + (month + 1) + "/" + year;
                                            else
                                                date = dayOfMonth + "/0" + (month + 1) + "/" + year;
                                            if (("" + dayOfMonth).length() == 1)
                                                date = "0" + date;
                                            Log.d("DATE FROM CALENDER VIEW", date);
                                            fetchOrders(mobileArrayAdapter.getItem(i), arrayAdapter.getItem(i), date.trim());
                                        }
                                    });

                                    btPdf.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                createPdfWrapper(i,false);
                                            } catch (FileNotFoundException e){
                                                e.printStackTrace();
                                            } catch (DocumentException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    btSharePdf.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                createPdfWrapper(i,true);
                                            } catch (FileNotFoundException e){
                                                e.printStackTrace();
                                            } catch (DocumentException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }
                        }
                    }
                });

        final TapTargetSequence sequence = new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forView(view.findViewById(R.id.text_view),"",getString(R.string.order_tutorial))
                                .outerCircleColor(R.color.red)
                                .outerCircleAlpha(0.75f)
                                .targetCircleColor(R.color.black)
                                .titleTextSize(30)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(20)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .targetRadius(60)
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(true)
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
            getActivity().getSharedPreferences("MAIN",MODE_PRIVATE).edit().putBoolean("order",false).apply();
        }


        return view;
    }

    void fetchOrders(final String documentID, final String milkmanName, final String calenderDate) {
        arrayList.clear();
        recyclerView.setVisibility(View.VISIBLE);
        btPdf.setVisibility(View.VISIBLE);
        btSharePdf.setVisibility(View.VISIBLE);
        notConnected.setVisibility(View.GONE);
        c=0;
        db.collection("Client").document(clientMobile).collection("DeliveryPerson").document(documentID).collection("Orders")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map;
                                map = (Map<String, Object>) document.get("Milk");
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String date = sdf.format(document.getTimestamp("timestamp").toDate());
                                SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
                                String Time = localDateFormat.format(document.getTimestamp("timestamp").toDate());
                                ClientOrder order = new ClientOrder(documentID, document.getId(), date, map, milkmanName, document.getLong("Amount")+"",Time);

                                if(calenderDate.equalsIgnoreCase("") || calenderDate.equalsIgnoreCase(date.trim())) {

                                    arrayList.add(c, order);
                                    ++c;
                                }
                            }
                            if(arrayList.size() == 0)
                            {
                                recyclerView.setVisibility(View.INVISIBLE);
                                btPdf.setVisibility(View.INVISIBLE);
                                btSharePdf.setVisibility(View.INVISIBLE);
                                notConnected.setVisibility(View.VISIBLE);
                                notConnected.setText(R.string.no_order_available);
                            }
                            else {
                                adapter = new ClientOrderAdapter(arrayList, getActivity(), getActivity());
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void attachID(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        spinner = view.findViewById(R.id.spinner);
        pgBar = view.findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        mobileArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        calendarView = view.findViewById(R.id.calender_view);
        nestedScrollView=view.findViewById(R.id.scroll_view);
        sideNavButton = view.findViewById(R.id.side_navbutton);
        notConnected = view.findViewById(R.id.not_connected);
        btPdf = view.findViewById(R.id.view_invoice);
        btSharePdf = view.findViewById(R.id.share_invoice);

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    private void createPdfWrapper(int i,Boolean share) throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel(getString(R.string.allow_storage_access),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            if (share) {
                showExportDialog(true,i);
            } else {
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(getActivity(), getActivity(), arrayAdapter.getItem(i), mobileArrayAdapter.getItem(i), share,"pdf");
                pd.show(getActivity().getSupportFragmentManager(), "MonthYearPickerDialog");}
        }
    }

    private void showExportDialog(final Boolean share, final int i) {
        customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.export_dialog);
        btExcel = customDialog.findViewById(R.id.btExportExcel);
        btExPdf = customDialog.findViewById(R.id.btExportPdf);
        btCancel = customDialog.findViewById(R.id.cancel);

        btExPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(getActivity(), getActivity(), arrayAdapter.getItem(i), mobileArrayAdapter.getItem(i), share,"pdf");
                pd.show(getActivity().getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        btExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(getActivity(), getActivity(), arrayAdapter.getItem(i), mobileArrayAdapter.getItem(i), share,"excel");
                pd.show(getActivity().getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }



}
