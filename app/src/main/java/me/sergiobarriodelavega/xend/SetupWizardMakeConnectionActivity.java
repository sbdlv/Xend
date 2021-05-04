package me.sergiobarriodelavega.xend;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class SetupWizardMakeConnectionActivity extends AppCompatActivity {
    private ImageView ivConnectionAnim;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String username, password, address, domain;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wizard_connection);

        ivConnectionAnim = findViewById(R.id.ivConnectionAnim);

        ((AnimatedVectorDrawable) ivConnectionAnim.getDrawable()).start();

        username = getIntent().getExtras().getString("username",null);
        password = getIntent().getExtras().getString("password",null);
        domain = getIntent().getExtras().getString("domain",null);
        address = getIntent().getExtras().getString("address",null);

        //Make new connection
        try {
            App.makeNewConnection(username, password, address, domain);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //If successful connection, save preferences
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
        System.exit(0);
    }
}