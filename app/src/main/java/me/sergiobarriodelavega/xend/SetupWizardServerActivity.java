package me.sergiobarriodelavega.xend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SetupWizardServerActivity extends AppCompatActivity {
    private TextInputEditText textServerAddress, txtDomain;
    private static int CONTINUE_SETUP = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wizard_server);

        textServerAddress = findViewById(R.id.txtServerAddress);
        textServerAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                isNotValidAddressField();
            }
        });

        txtDomain = findViewById(R.id.txtDomain);
        txtDomain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                isNotValidDomain();
            }
        });

    }

    public void continueSetup(View view){
        //Check formats
        if(!isNotValidAddressField() && !isNotValidDomain()){
            //Next wizard page
            Intent i = new Intent(this, SetupWizardLoginActivity.class);
            i.putExtra("address", textServerAddress.getText().toString());
            i.putExtra("domain", txtDomain.getText().toString());

            startActivityForResult(i, CONTINUE_SETUP);
        }
    }

    public void goBack(View view){
        finish();
    }

    /**
     * Check the address field.
     * @return True if the text is valid, False if not
     */
    private boolean isNotValidAddressField(){
        return Utils.isEmpty(textServerAddress) || Utils.hasSpaces(textServerAddress);
    }

    /**
     * Check the domain.
     * @return True if the text is valid, False if not
     */
    private boolean isNotValidDomain(){
        return Utils.isEmpty(txtDomain) || Utils.hasSpaces(txtDomain);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == SetupWizardMakeConnectionActivity.SUCCESSFUL_CONNECTION){
            finish();
        }
    }
}
