package it.grati_alexandru.provafinaleandroidacademy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import it.grati_alexandru.provafinaleandroidacademy.Model.Client;
import it.grati_alexandru.provafinaleandroidacademy.Model.User;
import it.grati_alexandru.provafinaleandroidacademy.Model.Package;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DateConversion;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FileOperations;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FirebaseRestRequests;
import it.grati_alexandru.provafinaleandroidacademy.Utils.Geodecode;

public class PackageActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView clientName;
    private TextView dAddress;
    private TextView dDate;
    private TextView size;
    private TextView sAddress;
    private Button bModifyStatus;
    private FragmentManager fragmentManager;
    private SupportMapFragment mapFragment;

    private List<LatLng> pointList;
    private LatLng ritiro;
    private LatLng consegna;
    private List<String> strings;
    private User user;
    private Package pack;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);
        clientName = findViewById(R.id.textViewClientName);
        dAddress = findViewById(R.id.textViewDelivery);
        dDate = findViewById(R.id.textViewDateID);
        size = findViewById(R.id.textViewSize);
        sAddress = findViewById(R.id.textViewStorage);
        bModifyStatus = findViewById(R.id.buttonModifyStatus);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user = (User) FileOperations.readObject(getApplicationContext(),"USER");

        int id = Integer.parseInt(sharedPreferences.getString("PACKAGE_ID", "-1"));
        pack = user.findPackageById(id);

        clientName.setText(pack.getClientName());
        dAddress.setText(pack.getClientAddress());
        dDate.setText(DateConversion.formatDateToString(pack.getDeliveryDate()));
        size.setText(pack.getSize());
        sAddress.setText(pack.getWarehouseAddress());

        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        fragmentManager = getSupportFragmentManager();

        ritiro = Geodecode.getLocationFromAddress(this,pack.getClientAddress());
        consegna = Geodecode.getLocationFromAddress(this,pack.getWarehouseAddress());


        fragmentManager.beginTransaction().add(R.id.packMapContainer,mapFragment).commit();

        if(user instanceof Client){
            if (pack.getStatus().equals("Commissionato")){
                bModifyStatus.setVisibility(View.INVISIBLE);
            }
            if(pack.getStatus().equals("Ritirato")){
                bModifyStatus.setText("Confirma");
            }
        }else{
            if (pack.getStatus().equals("Commissionato")){
                bModifyStatus.setText("Ritira");
            }
            if(pack.getStatus().equals("Ritirato")){
                bModifyStatus.setVisibility(View.INVISIBLE);
            }
        }
        if(pack.getStatus().equals("Confirmato")){
            bModifyStatus.setVisibility(View.INVISIBLE);
        }

    }

    public void onModifyStatusButtonClicked(View v){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String packId = setPackId(pack.getId());
        String url = FirebaseRestRequests.BASE_URL + "Users/Clients/" + pack.getClientUsername()+"/Packages/"+ packId;
        String urlPackage = FirebaseRestRequests.BASE_URL + "Packages/";
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl(url);
        DatabaseReference databasePackageReference = firebaseDatabase.getReferenceFromUrl(urlPackage);
        String bText = bModifyStatus.getText().toString();
        switch (bText){
            case "Confirma":
                databaseReference.child("Status").setValue("Confirmato");
                databaseReference.child("id").setValue(packId);
                databasePackageReference.child(packId).child("Status").setValue("Confirmato");
                pack.setStatus("Confirmato");
                break;
            case "Ritira":
                databaseReference.child("Status").setValue("Ritirato");
                databaseReference.child("id").setValue(packId);
                databasePackageReference.child(packId).child("Status").setValue("Ritirato");
                pack.setStatus("Ritirato");
                break;
        }
        user.modifyPackStatus(pack);
        FileOperations.writeObject(getApplicationContext(),"USER",user);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(ritiro).title("Ritiro"));
        googleMap.addMarker(new MarkerOptions().position(consegna).title("Consegna"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ritiro).zoom(14.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.moveCamera(cameraUpdate);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    public String setPackId(int id){
        if(id < 9){
            return "0"+id;
        }
        return  ""+id;
    }
}
