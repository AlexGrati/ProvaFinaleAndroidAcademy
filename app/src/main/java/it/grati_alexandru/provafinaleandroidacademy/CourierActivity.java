package it.grati_alexandru.provafinaleandroidacademy;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.Date;

import cz.msebera.android.httpclient.Header;
import it.grati_alexandru.provafinaleandroidacademy.Model.Package;
import it.grati_alexandru.provafinaleandroidacademy.Model.User;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DataParser;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DateConversion;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FileOperations;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FirebaseRestRequests;
import it.grati_alexandru.provafinaleandroidacademy.Utils.ResponseController;

import static it.grati_alexandru.provafinaleandroidacademy.Model.User.USER;

public class CourierActivity extends AppCompatActivity implements ResponseController {
    private TextView warehouseAddress;
    private TextView deliveryAddress;
    private TextView deliveryDate;
    private Spinner spinnerSize;
    private boolean hasEmptyFields;
    private String warning;
    private int lastId;
    private SharedPreferences sharedPreferences;
    private ResponseController responseController;
    private ProgressDialog progressDialog;
    private User user;
    private String courierUsername;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);
        warehouseAddress = findViewById(R.id.editTextWAdd);
        deliveryAddress = findViewById(R.id.editTextDAdd);
        deliveryDate = findViewById(R.id.editTextDDate);
        spinnerSize = findViewById(R.id.spinnerSizeId);

        responseController = this;
        progressDialog = new ProgressDialog(CourierActivity.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user = (User) FileOperations.readObject(getApplicationContext(),"USER");
        courierUsername = getIntent().getStringExtra("COURIER_USERNAME");
        type = sharedPreferences.getString("TYPE","");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("BACK_FROM_TAB","DASHBOARD");
        editor.apply();
    }

    public void onPlaceButtonClicked(View v){
        warning = checkEmptyFields();
        if(!hasEmptyFields){
            progressDialog.setTitle("Loading Data...");
            progressDialog.show();
            lastId = getPackageId();

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            String packageUrl = FirebaseRestRequests.BASE_URL + "Packages";
            DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl(packageUrl);
            DatabaseReference databaseReferenceLastPackId = firebaseDatabase.getReferenceFromUrl(FirebaseRestRequests.BASE_URL);
            String id = setId(lastId);

            Package pack = new Package();
            pack.setId(lastId);
            pack.setStatus(Package.STATUS_COMMISSIONATO);
            pack.setClientUsername(user.getUsername());
            pack.setCourierUsername(courierUsername);
            pack.setDeliveryDate(DateConversion.formatStringToDate(deliveryDate.getText().toString()));
            pack.setWarehouseAddress(warehouseAddress.getText().toString());
            pack.setClientAddress(deliveryAddress.getText().toString());
            pack.setClientName(user.getFirstName() + " " + user.getLastName());
            pack.setSize(spinnerSize.getSelectedItem().toString());

            //insert into packages
            databaseReference.child(id).child("ClientName").setValue(pack.getClientName());
            databaseReference.child(id).child("ClientUsername").setValue(pack.getClientUsername());
            databaseReference.child(id).child("DeliveryAddress").setValue(pack.getClientAddress());
            databaseReference.child(id).child("WarehouseAddress").setValue(pack.getWarehouseAddress());
            databaseReference.child(id).child("Status").setValue(pack.getStatus());
            databaseReference.child(id).child("Size").setValue(pack.getSize());
            databaseReference.child(id).child("DeliveryDate").setValue("00:00 "+deliveryDate.getText().toString());
            databaseReference.child(id).child("CourierUsername").setValue(courierUsername);
            insertIntoUser("Clients", user.getUsername());
            insertIntoUser("Couriers", courierUsername);

            lastId++;
            id = setId(lastId);
            databaseReferenceLastPackId.child("LastPackageId").setValue(id);
            user.modifyPackStatus(pack);
            FileOperations.writeObject(getApplicationContext(),USER,user);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),warning, Toast.LENGTH_SHORT).show();
        }
        responseController.respondOnRecevedData();
    }

    public void insertIntoUser(String userType, String username){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference;
        String id = setId(lastId);

        String userUrl = FirebaseRestRequests.BASE_URL+"Users/"+userType+"/"+username+"/Packages/";
        databaseReference = firebaseDatabase.getReferenceFromUrl(userUrl);
        if(userType.equals("Clients")) {
            databaseReference.child(id).child("Status").setValue("Commissionato");
        }
        databaseReference.child(id).child("id").setValue(id);
        databaseReference.child("LastModDate").setValue(DateConversion.formatDateToString(new Date()));
    }

    @Override
    public void respondOnRecevedData() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }

    public String setId(int id){
        if(id <= 9){
            id++;
            return ("0"+id);
        }
        return (""+id);
    }

    public int getPackageId(){
        FirebaseRestRequests.get("LastPackageId", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    String result = new String(responseBody);
                    result = DataParser.parseString(result);
                    lastId = Integer.parseInt(result);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        return 0;
    }

    public String checkEmptyFields(){
        hasEmptyFields = false;
        if(warehouseAddress.getText().toString().equals("")){
            hasEmptyFields = true;
            return "Insert Warehouse Address";
        }

        if(deliveryAddress.getText().toString().equals("")){
            hasEmptyFields = true;
            return "Insert Delivery Address";

        }
        if(deliveryDate.getText().toString().equals("")){
            hasEmptyFields = true;
            return "Insert Delivery Date";
        }

        return  "";
    }
}
