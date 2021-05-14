package me.sergiobarriodelavega.xend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SplashScreen extends AppCompatActivity {

    private ImageView ivSplashLogo;
    private BroadcastReceiver broadcastReceiverStartMainActivity;
    private BroadcastReceiver broadcastReceiverStartSetupWizard;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ivSplashLogo = findViewById(R.id.ivSplashLogo);

        //Splash Animation
        LayerDrawable ld = (LayerDrawable) getWindow().getDecorView().getBackground();
        AnimatedVectorDrawable ad = (AnimatedVectorDrawable) ld.findDrawableByLayerId(R.id.splash_animated_vector_drawable);
        ad.start();

        if(App.isBound()) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.preferences_xmpp_config), Context.MODE_PRIVATE);
            if(sharedPreferences.getBoolean("hasSetup", false)){
                startMain();
            } else {
                startSetupWizard();
            }

        } else {
            //Register BroadcastReceiver
            broadcastReceiverStartMainActivity = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    startMain();
                }
            };

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverStartMainActivity,
                    new IntentFilter(LocalBroadcastsEnum.SUCCESSFUL_CONNECTION));

            //Register BroadcastReceiver
            broadcastReceiverStartSetupWizard = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    startSetupWizard();
                }
            };

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverStartSetupWizard,
                    new IntentFilter(LocalBroadcastsEnum.START_SETUP_WIZARD));
        }


    }

    private void startMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void startSetupWizard(){
        startActivity(new Intent(getApplicationContext(), SetupWizardServerActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        //Unregister broadcast receivers
        if(broadcastReceiverStartMainActivity != null){
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiverStartMainActivity);
        }

        if(broadcastReceiverStartSetupWizard != null){
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiverStartSetupWizard);
        }

        super.onDestroy();
    }
}
