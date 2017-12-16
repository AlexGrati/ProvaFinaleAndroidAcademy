package it.grati_alexandru.provafinaleandroidacademy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import it.grati_alexandru.provafinaleandroidacademy.Model.Client;
import it.grati_alexandru.provafinaleandroidacademy.Model.Courier;
import it.grati_alexandru.provafinaleandroidacademy.Model.Package;
import it.grati_alexandru.provafinaleandroidacademy.Model.User;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DataParser;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DateConversion;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FileOperations;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FirebasePush;
import it.grati_alexandru.provafinaleandroidacademy.Utils.FirebaseRestRequests;
import it.grati_alexandru.provafinaleandroidacademy.Utils.ResponseController;

public class MainActivity extends AppCompatActivity implements ResponseController {
    private TextView editTextFirstName;
    private TextView editTextLastName;
    private TextView editTextUsername;
    private TextView editTextPassword;
    private TextView textViewBack;
    private RadioGroup radioGroup;
    private Button bLogin;
    private boolean existsEmptyField;
    private String savedUser;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String warning;
    private int chekedbuttonId;
    private SharedPreferences sharedPreferences;
    private ResponseController responseController;
    private ProgressDialog progressDialog;
    private SharedPreferences.Editor editor;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        radioGroup = findViewById(R.id.radioGroup);
        bLogin = findViewById(R.id.bLogin);
        textViewBack = findViewById(R.id.textViewBack);

        responseController = this;
        progressDialog = new ProgressDialog(MainActivity.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        savedUser = sharedPreferences.getString("USER","");
        if(!savedUser.equals("")){
            loginUser();
        }
    }

    public void onLoginButtonClicked(View view){
        if(editTextFirstName.getVisibility() == View.GONE){
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            chekedbuttonId = radioGroup.getCheckedRadioButtonId();
            warning = checkEmptyFields();
            if(!existsEmptyField){
                progressDialog.setTitle("Checking data...");
                progressDialog.show();
                validateData();
            }else{
                Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRegisterButtonClicked(View view){
        if(editTextFirstName.getVisibility() == View.GONE) {
            editTextFirstName.setVisibility(View.VISIBLE);
            editTextLastName.setVisibility(View.VISIBLE);
            textViewBack.setVisibility(View.VISIBLE);
            bLogin.setVisibility(View.GONE);
        }else{
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            firstName = editTextFirstName.getText().toString();
            lastName = editTextLastName.getText().toString();
            chekedbuttonId = radioGroup.getCheckedRadioButtonId();
            warning = checkEmptyFields();

            if(!existsEmptyField){
                progressDialog.setTitle("Checking data...");
                progressDialog.show();
                registerUser();
                responseController.respondOnRecevedData();
                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onBackClicked(View v){
        editTextFirstName.setVisibility(View.GONE);
        editTextLastName.setVisibility(View.GONE);
        bLogin.setVisibility(View.VISIBLE);
        textViewBack.setVisibility(View.GONE);
    }

    @Override
    public void respondOnRecevedData() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }

    public String checkEmptyFields(){
        existsEmptyField = false;

        if(radioGroup.getVisibility() == View.VISIBLE && chekedbuttonId == -1 ){
            existsEmptyField = true;
            return "Select acount type";
        }

        if(editTextFirstName.getVisibility() == View.VISIBLE && firstName.equals("")){
            existsEmptyField = true;
                return "Insert First Name";
        }

        if(editTextLastName.getVisibility() == View.VISIBLE && lastName.equals("")){
            existsEmptyField = true;
            return "Insert Last Name";
        }

        if(editTextUsername.getVisibility() == View.VISIBLE && username.equals("")){
            existsEmptyField = true;
            return "Insert Username";
        }

        if(editTextPassword.getVisibility() == View.VISIBLE && password.equals("")){
            existsEmptyField = true;
            return "Insert Password";
        }
        return  "";
    }

    public void validateData(){
        String urlElem = setUrlElement();
        String url = "Users/"+urlElem+"/"+username+"/Password";
        FirebaseRestRequests.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200) {
                    String response = new String(responseBody);
                    if(!response.equals("null")){
                        response = DataParser.parseString(response);
                        if(response.equals(password)){
                            saveUserToPreferences();
                            loginUser();
                            Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Wrong Username", Toast.LENGTH_SHORT).show();
                    }
                }
                responseController.respondOnRecevedData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode >= 400 && statusCode < 500){
                    Toast.makeText(getApplicationContext(), "Server Unavailable", Toast.LENGTH_SHORT).show();
                }
                responseController.respondOnRecevedData();
            }
        });
    }

    public void loginUser(){
        Intent intent = new Intent(getApplicationContext(),BottomNavigationActivity.class);
        startActivity(intent);
        if(user == null){
            User user = new User();
            user.setUsername(username);
        }
        FileOperations.writeObject(getApplicationContext(),"User", user);
        Intent serviceIntent = new Intent(getApplicationContext(), FirebasePush.class);
        startService(serviceIntent);
        finish();
    }

    public void registerUser(){
        String urlElem = setUrlElement();
        String url = FirebaseRestRequests.BASE_URL + "Users/"+urlElem;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseLastModDate = firebaseDatabase.getReferenceFromUrl(url);
        databaseLastModDate.child("LastRegistration").setValue(DateConversion.formatDateToString(new Date()));

        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl(url);
        databaseReference.child(username).child("FirstName").setValue(firstName);
        databaseReference.child(username).child("LastName").setValue(lastName);
        databaseReference.child(username).child("Password").setValue(password);

        if(urlElem.equals("Clients")){
            user = new Client(firstName,lastName,username,password, new ArrayList<Package>());
        }else{
            user = new Courier(firstName,lastName,username,password, new ArrayList<Package>());
        }
        saveUserToPreferences();
        loginUser();
        finish();
    }

    public void saveUserToPreferences(){
        editor.putString("USER",username);
        editor.apply();
    }

    public String setUrlElement(){
        View v = radioGroup;
        RadioButton radioButton = v.findViewById(chekedbuttonId);
        String type;
        if(radioButton.getText().toString().equals("Client"))
            type = "Clients";
        else type = "Couriers";
        editor.putString("TYPE",type);
        editor.apply();
        return type;
    }
}