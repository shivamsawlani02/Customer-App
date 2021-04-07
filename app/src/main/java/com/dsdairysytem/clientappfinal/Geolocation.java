package com.dsdairysytem.clientappfinal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.kloadingspin.KLoadingSpin;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.core.GeoHash;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

public class Geolocation extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private LocationCallback locationCallback;
    Button store,skip;
     ArrayList<String> listofdoc=new ArrayList<>();

    private Location location;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE= 100;
    String mob;
    String name;
    TextView nearyou;
    List<Map<String, Object>> milktype;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    ArrayList<String> arrayList=new ArrayList<>();
    ArrayList<String > arrayList1=new ArrayList<>();
    Button button_add;
    String clientMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

    LinearLayout linearLayouth;



KLoadingSpin kLoadingSpin;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_geolocation);
     //  store=findViewById(R.id.storelocation);
        bottom_sheet = findViewById(R.id.bottomsheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        linearLayouth=findViewById(R.id.hidinglayout);
        skip = findViewById(R.id.skip);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }


        kLoadingSpin=findViewById(R.id.KLoadingSpin);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Geolocation.this,MainActivity.class));
                finish();
            }
        });


        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this,"AIzaSyDMeBkGaEhoNmmKEieJJKIIT6EgTrTISyY");
        placesClient=Places.createClient(this);
        final AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();
        fetchlocation();

        final TextView buttonshhet=findViewById(R.id.bottomrecom);

//        buttonshhet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                 //   buttonshhet.setText("Close sheet");
//                } else {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                  //  buttonshhet.setText("Expand sheet");
//                }
//            }
//        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                       // buttonshhet.setText("Close Sheet");
                        linearLayouth.setVisibility(View.VISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                       // buttonshhet.setText("Expand Sheet");
                        linearLayouth.setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });









        buttonshhet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listofdoc.clear();

                kLoadingSpin.startAnimation();
                kLoadingSpin.setIsVisible(true);

                final CollectionReference doc = FirebaseFirestore.getInstance().collection("Client");
                GeoFirestore geoFirestore = new GeoFirestore(doc);
                if (location != null)
                {

                    GeoHash geoHash = new GeoHash(location.getLatitude(), location.getLongitude());
                String res = geoHash.getGeoHashString();
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                geoFirestore.setLocation(clientMobile, geoPoint);


                GeoQuery geoQuery;
                geoQuery = geoFirestore.queryAtLocation(geoPoint, 5);
                geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                    @Override
                    public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                        if (documentSnapshot.exists()) {
                            String id = documentSnapshot.getId().toString();
                            Log.d("AG", "onDocumentEntered: " + id);

                            listofdoc.add(id);


                        }

                    }

                    @Override
                    public void onDocumentExited(DocumentSnapshot documentSnapshot) {

                    }

                    @Override
                    public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                    }

                    @Override
                    public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                    }

                    @Override
                    public void onGeoQueryReady() {

//                        Intent intent=new Intent(getApplicationContext(),Recomendations.class);
//                        intent.putStringArrayListExtra("docid",listofdoc);
//
//                        Log.d("TAG", "onGeoQueryReady: "+listofdoc.size());
//                        startActivity(intent);

                        kLoadingSpin.stopAnimation();

                        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            //   buttonshhet.setText("Close sheet");
                            linearLayouth.setVisibility(View.VISIBLE);

                        } else {
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            //  buttonshhet.setText("Expand sheet");
                            linearLayouth.setVisibility(View.GONE);


                        }

                        getdetailsof();
                    }


                    @Override
                    public void onGeoQueryError(Exception e) {


                    }
                });
            }




            }
        });

    }

    public void getdetailsof(){

        ArrayList<String> docid=listofdoc;

        final Cardadapter adapter=new Cardadapter(getApplicationContext());

        nearyou=findViewById(R.id.nearyoutext);

        Intent intent=getIntent();



        final HListView listView=findViewById(R.id.list_cards);

        String t=Integer.toString(docid.size());


        listView.setAdapter(adapter);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);


        for (int i=0;i<docid.size();i++){

            if(!docid.get(i).equals(clientMobile)){

                Query collectionReference=db.collection("Client").document(docid.get(i)).collection("DeliveryPerson")
                        .whereEqualTo("Type","DeliveryPerson");

                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("tagging", "onComplete: getting");

                        if (task.isSuccessful()){
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()){
                                    Log.d("AG", "onComplete: "+arrayList.size());

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
                                                    Log.d("entering", "enterd: " +documentSnapshot);
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
                                    arrayList.clear();
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        }

                    }
                });

            }

        }
        if(docid.size()==1){
        //    nearyou.setText("NO DELIVERY PERSON FOUND");
        }



      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Toast.makeText(Geolocation.this,  String.valueOf(id), Toast.LENGTH_SHORT).show();


              Carddetails carddetails=adapter.getItem(position);

              carddetails.getTitle();
              String  mob_alert=carddetails.getSubtitle();
              String  name_alert=carddetails.getTitle();

              Log.d("sa", "onItemClick: "+carddetails.getTitle());
              dialog_box(view,mob_alert,name_alert);


          }
      });


        docid.clear();

    }



    private void fetchlocation() {
//        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//            return;

        if (ContextCompat.checkSelfPermission(Geolocation.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Geolocation.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(Geolocation.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }else{
                ActivityCompat.requestPermissions(Geolocation.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
        }



        Task<Location> task =fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location locations) {
                if(locations !=null){
                    Log.d("LOCATIONS","NOT NULL");
                    location=locations;
                    Toast.makeText(Geolocation.this, "getting"+locations.getLongitude()+locations.getLatitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(Geolocation.this);

                }else{
                    Log.d("LOCATIONS","NULL");
                    final LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setInterval(10000);
                    locationRequest.setFastestInterval(5000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationCallback=new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            if(locationResult==null)
                                return;

                            location=locationResult.getLastLocation();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.getLatitude(), locations.getLongitude()), 18));
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }
                    };

                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

      //  MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("you");

        //googleMap.addMarker(markerOptions);

        mMap=googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker")
                .draggable(true)
                .snippet("Hello")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                LatLng pos=marker.getPosition();
                location.setLatitude(pos.latitude);
                location.setLongitude(pos.longitude);

            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchlocation();
                }
                break;

        }
    }

  public void dialog_box(View view, final String mobile, final String name){
      final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
      final ViewGroup viewGroup = findViewById(android.R.id.content);
      final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_box, viewGroup, false);
      //Button buttonOk=dialogView.findViewById(R.id.buttonOk);
      builder.setView(dialogView);
      final AlertDialog alertDialog = builder.create();
//      buttonOk.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//              alertDialog.dismiss();
//          }
//      })
      DocumentReference documentReference=db.collection("Delivery").document("+919988776655");
      documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if (task.isSuccessful()){
                  DocumentSnapshot documentSnapshot=task.getResult();
                  if (documentSnapshot.exists()){
                      Log.d("entering", "enterdonemore: " +documentSnapshot);
                      List<Map<String, Object>> milktype1 = (List<Map<String, Object>>) documentSnapshot.get("Products");
                      int k1=milktype.size();

                      for(int j=0;j<k1;j++){
                          arrayList1.add((String) milktype1.get(j).get("Quality"));
                      }


                      ArrayAdapter<String>  arrayAdapter=new ArrayAdapter<String>(getBaseContext(),R.layout.simple_string_list,R.id.miltext,arrayList);

                      arrayAdapter.notifyDataSetChanged();

                      TextView textView_name=dialogView.findViewById(R.id.name_alert);
                      TextView textView_mob=dialogView.findViewById(R.id.mobile_alert);

                      textView_name.setText(name);
                      textView_mob.setText(mobile);

                      ListView listView_dialog=dialogView.findViewById(R.id.milklist_dialog);
                        if(!arrayAdapter.isEmpty()){
                             listView_dialog.setAdapter(arrayAdapter);}

                        arrayList1.clear();

                  }
              }
          }
      });
      button_add=dialogView.findViewById(R.id.add_alert);
      button_add.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              //Add document of client number to deliver person

//              DocumentReference documentReference1=db.collection("Delivery").document(mobile).
//                      collection("Client").document("Client_phone_number");
//
              final AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext(),R.style.CustomAlertDialog);
              final ViewGroup viewGroup = findViewById(android.R.id.content);
              final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.successfully_added, viewGroup, false);
              //Button buttonOk=dialogView.findViewById(R.id.buttonOk);
              builder.setView(dialogView);
              final AlertDialog alertDialog = builder.create();
              alertDialog.show();


          }
      });
      alertDialog.show();
  }




  }

