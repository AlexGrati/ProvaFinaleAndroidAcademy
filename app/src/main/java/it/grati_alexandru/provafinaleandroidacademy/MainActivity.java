package it.grati_alexandru.provafinaleandroidacademy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import it.grati_alexandru.provafinaleandroidacademy.Utils.ResponseController;

public class MainActivity extends AppCompatActivity implements ResponseController {
    private TextView editTextFirstName;
    private TextView editTextLastName;
    private TextView editTextUsername;
    private TextView editTextPassword;
    private RadioGroup radioGroup;
    private Button bLogin;
    private boolean existsEmptyField;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String warning;

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

    }

    public void onLoginButtonClicked(View view){
        if(editTextFirstName.getVisibility() == View.GONE){
            warning = checkEmptyFields();
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            if(!existsEmptyField){
                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRegisterButtonClicked(View view){
        if(editTextFirstName.getVisibility() == View.GONE) {
            editTextFirstName.setVisibility(View.VISIBLE);
            editTextLastName.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
            bLogin.setVisibility(View.INVISIBLE);
        }else{
            warning = checkEmptyFields();
            if(!existsEmptyField){
                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void respondOnRecevedData() {

    }

    public String checkEmptyFields(){
        String warning = "";
        existsEmptyField = false;

        if(editTextFirstName.getVisibility() == View.VISIBLE && firstName.equals("")){
            existsEmptyField = true;
            if(warning.equals("")){
                warning = "Insert First Name";
            }
        }

        if(editTextLastName.getVisibility() == View.VISIBLE && lastName.equals("")){
            existsEmptyField = true;
            if(warning.equals("")){
                warning = "Insert Last Name";
            }else{
                warning = warning + ", last name";
            }
        }

        if(editTextUsername.getVisibility() == View.VISIBLE && username.equals("")){
            existsEmptyField = true;
            if(warning.equals("")){
                warning = "Insert username,";
            }else{
                warning = warning + ", username";
            }
        }

        if(editTextPassword.getVisibility() == View.VISIBLE && password.equals("")){
            existsEmptyField = true;
            if(warning.equals("")){
                warning = "Insert password";
            }else{
                warning = warning + ", password";
            }
        }
        return  warning;
    }
}

