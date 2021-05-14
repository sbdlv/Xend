package me.sergiobarriodelavega.xend;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SetupWizardMakeConnectionActivity extends AppCompatActivity {
    private ImageView ivConnectionAnim;
    public static final int ERROR_CONNECTION = 500, SUCCESSFUL_CONNECTION = 200;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String username, password, address, domain;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wizard_connection);

        ivConnectionAnim = findViewById(R.id.ivConnectionAnim);

        ((AnimatedVectorDrawable) ivConnectionAnim.getDrawable()).start();

        username = getIntent().getExtras().getString("username", null);
        password = getIntent().getExtras().getString("password", null);
        domain = getIntent().getExtras().getString("domain", null);
        address = getIntent().getExtras().getString("address", null);


        //Make new connection
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    App.tryNewConnection(username, password, address, domain);

                    //All ok, start Main Activity
                    Intent i = new Intent(SetupWizardMakeConnectionActivity.this, MainActivity.class);
                    startActivity(i);

                    setResult(SUCCESSFUL_CONNECTION);
                    finish();
                } catch (Exception e) {
                    setResult(ERROR_CONNECTION);
                    finish();
                }
            }
        }).start();
    }
}
