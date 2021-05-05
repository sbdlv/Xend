package me.sergiobarriodelavega.xend.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.sergiobarriodelavega.xend.SetupWizardServerActivity;

public class StartSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(this, SetupWizardServerActivity.class);
        startActivity(i);
        finish();
    }
}
