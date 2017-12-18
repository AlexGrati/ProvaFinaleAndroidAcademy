package it.grati_alexandru.provafinaleandroidacademy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import it.grati_alexandru.provafinaleandroidacademy.Fragments.CourierFragment;
import it.grati_alexandru.provafinaleandroidacademy.Fragments.PackageFragment;
import it.grati_alexandru.provafinaleandroidacademy.Model.Client;
import it.grati_alexandru.provafinaleandroidacademy.Model.Courier;
import it.grati_alexandru.provafinaleandroidacademy.Model.Package;
import it.grati_alexandru.provafinaleandroidacademy.Model.User;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DataParser;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FileOperations;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FirebaseRestRequests;
import it.grati_alexandru.provafinaleandroidacademy.Utils.ResponseController;

public class BottomNavigationActivity extends AppCompatActivity  implements ResponseController{

    //private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private SupportMapFragment supportMapFragment;
    private FragmentTransaction fragmentTransaction;
    private User user;
    private SharedPreferences sharedPreferences;
    private ResponseController responseController;
    private ProgressDialog progressDialog;
    private List<Package> packageList;
    private String type;
    private Gson gson;
    private List<Courier> courierList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.baseFrameLayout,new PackageFragment()).commit();
                    return true;
                case R.id.navigation_dashboard:
                    if(user instanceof Client)
                        checkCourierList();
                    else{
                        supportMapFragment.getMapAsync(new MapFragmentActivity(getApplicationContext()));
                        fragmentManager.beginTransaction().add(R.id.baseFrameLayout,supportMapFragment).commit();
                    }
                    return true;
            }
            return false;
        }
    };

    public void checkCourierList(){
        gson = new Gson();
        String json = sharedPreferences.getString("COURIER_LIST","");
        Type type = new TypeToken<List<Courier>>(){}.getType();
        courierList = gson.fromJson(json,type);
        if(courierList == null){
            getAllCouriersFormFirebase();
        }else{
            fragmentTransaction.replace(R.id.baseFrameLayout,new CourierFragment()).commit();
        }
    }

    public void getAllCouriersFormFirebase(){
        progressDialog.setTitle("Loading Data..");
        progressDialog.show();
        String url = "Users/Couriers";
        FirebaseRestRequests.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    String response = new String(responseBody);
                    List<Courier> courierList = DataParser.cerateCourierList(response);
                    courierList.size();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String jsString = gson.toJson(courierList);
                    editor.putString("COURIER_LIST",jsString);
                    editor.apply();
                    fragmentTransaction.replace(R.id.baseFrameLayout,new CourierFragment()).commit();
                }
                responseController.respondOnRecevedData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode >= 400 && statusCode <500){
                    Toast.makeText(getApplicationContext(), "Server Unavailable", Toast.LENGTH_SHORT).show();
                }
                responseController.respondOnRecevedData();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //toolbar = (Toolbar) findViewById(R.id.toolbarID);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user = (User) FileOperations.readObject(getApplicationContext(),"USER");
        type = sharedPreferences.getString("TYPE","");
        progressDialog = new ProgressDialog(BottomNavigationActivity.this);
        responseController = this;

        if(user != null && user.getPackageList().size() > 0){
            packageList = user.getPackageList();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.baseFrameLayout,new PackageFragment()).commit();
        }else {
            /*if(type.equals("Couriers"))
                user = new Courier();
            else user = new Client();*/
            getPackagesFormFirebase();
        }
    }

   @Override
    public void onResume(){
        super.onResume();
        getPackagesFormFirebase();
    }

    public void createPackageListFromId(final List<Integer> packageIdList){
        String url = "Packages/";
        FirebaseRestRequests.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200) {
                    List<Package> tempPackageList;
                    String response = new String(responseBody);
                    tempPackageList = DataParser.createPackageListFromId(response, packageIdList);
                    tempPackageList.size();
                    Log.i("TEST", ""+tempPackageList.get(0).getClientAddress() + " "+ tempPackageList.get(0).getWarehouseAddress());
                    user.setPackageList(tempPackageList);
                    FileOperations.writeObject(getApplicationContext(), "USER", user);
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.baseFrameLayout,new PackageFragment()).commit();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode >= 400 && statusCode <500){
                    Toast.makeText(getApplicationContext(), "Server Unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getPackagesFormFirebase(){
        progressDialog.setTitle("Loading Data..");
        progressDialog.show();
        String username = sharedPreferences.getString("USER", "");
        String url = "Users/" + type + "/" + username + "/Packages";
        FirebaseRestRequests.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    String response = new String(responseBody);
                    List<Integer> packageListId = DataParser.getPackagesId(response);
                    packageListId.size();
                    createPackageListFromId(packageListId);
                    Toast.makeText(getApplicationContext(), "Loading Done", Toast.LENGTH_SHORT).show();
                }
                responseController.respondOnRecevedData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode >= 400 && statusCode <500){
                    Toast.makeText(getApplicationContext(), "Server Unavailable", Toast.LENGTH_SHORT).show();
                }
                responseController.respondOnRecevedData();
            }
        });
    }

    @Override
    public void respondOnRecevedData() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
    }
    */
}
