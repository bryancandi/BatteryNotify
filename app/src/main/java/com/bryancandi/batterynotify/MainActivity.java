package com.bryancandi.batterynotify;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.widget.Button;
import android.widget.ToggleButton;

/**
 * Created by quake on 9/13/2016.
 */
public class MainActivity extends AppCompatActivity {

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    ToggleButton batteryToggle;
    ToggleButton bootToggle;
    Button permissionButton;

    @Override
    public void onResume() {
        super.onResume();
        prefs = this.getSharedPreferences("bootpref", Context.MODE_PRIVATE);
        prefs = this.getSharedPreferences("notipref", Context.MODE_PRIVATE);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryToggle = (ToggleButton) findViewById(R.id.tBatteryNotification);
        batteryToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (batteryToggle.isChecked()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("notipref", true);
                editor.apply();
                enableBatteryNotification();
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("notipref", false);
                editor.apply();
                disableBatteryNotification();
            }
        });

        bootToggle = (ToggleButton) findViewById(R.id.tBootStartup);
        bootToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bootToggle.isChecked()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("bootpref", true);
                editor.apply();
                enableServiceOnBoot();
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("bootpref", false);
                editor.apply();
                disableServiceOnBoot();
            }
        });

        permissionButton = (Button) findViewById(R.id.buttonPermission);
        permissionButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplication().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
    }

    public void enableBatteryNotification() {
        batteryToggle.setChecked(true);
        startService(new Intent(this, BatteryNotification.class));
    }

    public void disableBatteryNotification() {
        batteryToggle.setChecked(false);
        stopService(new Intent(getBaseContext(), BatteryNotification.class));
    }

    public void enableServiceOnBoot() {
        bootToggle.setChecked(true);
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        BootStartup.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void disableServiceOnBoot() {
        bootToggle.setChecked(false);
        PackageManager pm = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        BootStartup.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}