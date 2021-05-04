package me.sergiobarriodelavega.xend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SetupWizardLoginActivity extends AppCompatActivity {
    private TextInputEditText txtUsername, txtPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wizard_login);

        txtPassword = findViewById(R.id.txtPassword);
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                isNotValidPassword();

            }
        });

        txtUsername = findViewById(R.id.txtUserName);
        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                isNotValidUserName();
            }
        });


    }

    public void continueSetup(View view){

        if(!isNotValidUserName() && !isNotValidPassword()){
            Intent i = new Intent(this, SetupWizardMakeConnectionActivity.class);
            Bundle b = getIntent().getExtras();
            b.putString("username", txtUsername.getText().toString());
            b.putString("password", txtPassword.getText().toString());

            startActivity(i);
        }
    }

    private boolean isNotValidUserName(){
        return Utils.isEmpty(txtUsername) || Utils.hasSpaces(txtUsername);
    }

    private boolean isNotValidPassword(){
        return Utils.isEmpty(txtPassword) || Utils.hasSpaces(txtPassword);
    }
}
