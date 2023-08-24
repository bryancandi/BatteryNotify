package com.bryancandi.batterynotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by quake on 9/14/2016.
 */
    public class BootStartup extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        startService(new Intent(this, BatteryNotification.class));
        Intent startServiceIntent = new Intent(context, BatteryNotification.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(startServiceIntent);
        } else {
            context.startForegroundService(startServiceIntent);
        }
    }
}

