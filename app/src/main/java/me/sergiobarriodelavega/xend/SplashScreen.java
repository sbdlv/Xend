package me.sergiobarriodelavega.xend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.List;

import me.sergiobarriodelavega.xend.entities.XMPPUser;

public class SplashScreen extends AppCompatActivity {

    private ImageView ivSplashLogo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ivSplashLogo = findViewById(R.id.ivSplashLogo);

        LayerDrawable ld = (LayerDrawable) getWindow().getDecorView().getBackground();
        AnimatedVectorDrawable ad = (AnimatedVectorDrawable) ld.findDrawableByLayerId(R.id.drawable_anim);
        ad.start();

        new MakeConnection().execute();
    }

    private class MakeConnection extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                App.getConnection();
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}