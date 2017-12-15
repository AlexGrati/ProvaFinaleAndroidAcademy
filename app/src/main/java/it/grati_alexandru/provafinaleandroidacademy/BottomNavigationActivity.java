package it.grati_alexandru.provafinaleandroidacademy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
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

    private TextView mTextMessage;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private User user;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences sharedPreferences;
    private ResponseController responseController;
    private ProgressDialog progressDialog;
    private Package aPackage;
    private String savedUser;
    private List<Package> packageList;
    private String type;
    private Context context;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.baseFrameLayout,new PackageFragment()).commit();
                    return true;
                case R.id.navigation_dashboard:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        user = (User) FileOperations.readObject(getApplicationContext(),"USER");
        if(user != null && user.getPackageList().size() > 0){
            packageList = user.getPackageList();
        }else {
            if(type.equals("Couriers"))
                user = new Courier();
            else user = new Client();
            getPackagesFormFirebase();
        }
    }

    public void createPackageListFromId(final List<Integer> packageIdList){
        String url = "Packages/";
        FirebaseRestRequests.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Package> tempPackageList;
                String response = new String(responseBody);
                tempPackageList = DataParser.createPackageListFromId(response, packageIdList);
                tempPackageList.size();
                user.setPackageList(tempPackageList);
                FileOperations.writeObject(getApplicationContext(),"USER", user);
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

}
