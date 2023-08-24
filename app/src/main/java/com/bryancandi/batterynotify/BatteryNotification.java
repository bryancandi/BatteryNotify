package com.bryancandi.batterynotify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class BatteryNotification extends Service {

    int level;
    int temp;
    int plugtype;
    int status;

    String CHANNEL_ID = "battery_channel_id";
    int SERVICE_ID = 1001;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {

            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            //int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            plugtype = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            //int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        batteryStatNotification();
        }
    };

    private String plugTypeString(int plugtype) {
        String plugType = getResources().getString(R.string.unplugged);

        switch (plugtype) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = getResources().getString(R.string.AC);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = getResources().getString(R.string.USB);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                plugType = getResources().getString(R.string.wireless);
                break;
            case BatteryManager.BATTERY_STATUS_FULL: //this doesn't work right now...
                plugType = getResources().getString(R.string.full);
                break;
            //case BatteryManager.BATTERY_STATUS_DISCHARGING:
                //plugType = getResources().getString(R.string.discharge);
                //break;
        }
        return plugType;
    }

    public int smallIconInt(int smallicon) {
        int smallIcon = R.mipmap.ic_notification;

        switch (smallicon) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                smallIcon = R.mipmap.ic_notification_charging;
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                smallIcon = R.mipmap.ic_notification_full;
                break;
        }
        return smallIcon;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int StartId) {
        this.registerReceiver(this.batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return START_STICKY;
    }

/*    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = (getResources().getString(R.string.title) + level + "% " + (char) 0x2022 + " " + Integer.toString(temp / 10) + (char) 0x00B0 + "C " + (char) 0x2022 + " " +
                    Integer.toString((((temp / 10) * 9) / 5) + 32) + (char) 0x00B0 + "F");
            String description = (plugTypeString(plugtype));
            //CharSequence name = getString(R.string.channel_name);
            //String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            //channel.enableLights(true);
            //channel.setLightColor(Color.RED);
            //channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    } */

    public void batteryStatNotification() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSmallIcon(smallIconInt(status)) //get plug icon from int, based on case (BatteryReceiver)
                .setContentTitle(getResources().getString(R.string.title) + level + "% " + (char) 0x2022 + " " + temp / 10 + (char) 0x00B0 + "C " + (char) 0x2022 + " " +
                        ((((temp / 10) * 9) / 5) + 32) + (char) 0x00B0 + "F")

                .setContentText(plugTypeString(plugtype))
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(false);
            //channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //manager.notify(2, builder.build()); //Notification Id 2 = Battery
        startForeground(SERVICE_ID, notification);

    }

    @Override
    public void onDestroy() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(SERVICE_ID);
        unregisterReceiver(batteryReceiver);
    }
}