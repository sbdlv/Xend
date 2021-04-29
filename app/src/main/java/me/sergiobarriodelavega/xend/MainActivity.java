package me.sergiobarriodelavega.xend;

import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.NonSdkApiUsedViolation;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smackx.vcardtemp.VCardManager;

import java.io.IOException;
import java.util.List;

import me.sergiobarriodelavega.xend.room.LastChattedUser;
import me.sergiobarriodelavega.xend.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity implements View.OnLayoutChangeListener {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.addOnLayoutChangeListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_chats, R.id.nav_contacts, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Smack
        AndroidSmackInitializer.initialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean openEditProfile(MenuItem item){
        Intent i = new Intent(getApplicationContext(), ProfileEditActivity.class);
        startActivity(i);
        return true;
    }

    public boolean deleteRecentChats(MenuItem item){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getDb(getApplicationContext()).lastChattedUsersDAO().deleteAll();
            }
        }).start();

        return true;
    }

    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        TextView tvUserNameHeader = view.findViewById(R.id.tvUserNameHeader);
        TextView tvUserJIDHeader = view.findViewById(R.id.tvUserJIDHeader);

        try {
            //TODO UserName loading too
            tvUserJIDHeader.setText(App.getConnection().getUser().asBareJid().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}