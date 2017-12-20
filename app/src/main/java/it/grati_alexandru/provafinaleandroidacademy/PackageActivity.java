package it.grati_alexandru.provafinaleandroidacademy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import it.grati_alexandru.provafinaleandroidacademy.Fragments.PackageFragment;
import it.grati_alexandru.provafinaleandroidacademy.Model.Client;
import it.grati_alexandru.provafinaleandroidacademy.Model.User;
import it.grati_alexandru.provafinaleandroidacademy.Model.Package;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DateConversion;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FileOperations;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FirebaseRestRequests;
import it.grati_alexandru.provafinaleandroidacademy.Utils.Geodecode;

import static it.grati_alexandru.provafinaleandroidacademy.Model.Package.STATUS_COMMISSIONATO;
import static it.grati_alexandru.provafinaleandroidacademy.Model.Package.STATUS_CONFERMATO;
import static it.grati_alexandru.provafinaleandroidacademy.Model.Package.STATUS_RITIRATO;
import static it.grati_alexandru.provafinaleandroidacademy.Model.User.USER;

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
        user = (User) FileOperations.readObject(getApplicationContext(), USER);

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
            if (pack.getStatus().equals(STATUS_COMMISSIONATO)){
                bModifyStatus.setVisibility(View.INVISIBLE);
            }
            if(pack.getStatus().equals(STATUS_RITIRATO)){
                bModifyStatus.setText("Conferma");
            }
        }else{
            if (pack.getStatus().equals(STATUS_COMMISSIONATO)){
                bModifyStatus.setText("Ritira");
            }
            if(pack.getStatus().equals(STATUS_RITIRATO)){
                bModifyStatus.setVisibility(View.INVISIBLE);
            }
        }
        if(pack.getStatus().equals(STATUS_CONFERMATO)){
            bModifyStatus.setVisibility(View.INVISIBLE);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("BACK_FROM_TAB","HOME");
        editor.apply();

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
            case "Conferma":
                databaseReference.child("Status").setValue(STATUS_CONFERMATO);
                databaseReference.child("id").setValue(packId);
                databasePackageReference.child(packId).child("Status").setValue(STATUS_CONFERMATO);
                pack.setStatus(STATUS_CONFERMATO);
                break;
            case "Ritira":
                databaseReference.child("Status").setValue(STATUS_RITIRATO);
                databaseReference.child("id").setValue(packId);
                databasePackageReference.child(packId).child("Status").setValue(STATUS_RITIRATO);
                pack.setStatus(STATUS_RITIRATO);
                break;
        }
        user.modifyPackStatus(pack);
        FileOperations.writeObject(getApplicationContext(),USER,user);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            user = null;
            FileOperations.writeObject(getApplicationContext(),User.USER,user);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
