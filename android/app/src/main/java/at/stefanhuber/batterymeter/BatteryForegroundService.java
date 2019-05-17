package at.stefanhuber.batterymeter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class BatteryForegroundService extends Service {

    public static final String LOG_TAG = "BATTERY_METERING";

    public static final String ACTION_PAUSE_METERING = "at.stefanhuber.batterymeter.action.ACTION_PAUSE";
    public static final String ACTION_STOP_METERING  = "at.stefanhuber.batterymeter.action.ACTION_STOP";
    public static final String ACTION_UNPAUSE_METERING  = "at.stefanhuber.batterymeter.action.ACTION_UNPAUSE";

    protected volatile String state = "INIT";
    protected Thread worker;
    protected int interval = 10000;
    protected String name = "";
    protected int sessionId = -1;
    protected Metering metering;
    protected BatteryManager batteryManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (metering == null) {
            metering = new Metering(this);
        }
        if (batteryManager == null) {
            batteryManager = getSystemService(BatteryManager.class);
        }
        if (intent.hasExtra("name") && intent.hasExtra("interval") && state == "INIT") {
            name = intent.getStringExtra("name");
            interval = intent.getIntExtra("interval", 10000);
        }

        if (worker != null &&
            intent.getAction() != null &&
            intent.getAction().equals(ACTION_PAUSE_METERING)) {
            state = "PAUSED";
            update();
        } else if (worker != null &&
            intent.getAction() != null &&
            intent.getAction().equals(ACTION_STOP_METERING)) {
            state = "STOPPED";
            update();
        } else if (worker != null &&
            intent.getAction() != null &&
            intent.getAction().equals(ACTION_UNPAUSE_METERING)) {
            state = "STARTED";
            update();
        } else {
            state = "STARTED";
            start();
        }

        return START_STICKY;
    }

    protected void update() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(sessionId, createNotification());
    }

    protected void start() {
        BatteryForegroundService.this.sessionId = metering.startSession(name);
        BatteryForegroundService.this.startForeground(sessionId, createNotification());

        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (BatteryForegroundService.this.state.equals("STOPPED")) {
                            break;
                        } else if (BatteryForegroundService.this.state.equals("PAUSED")) {
                            Log.i(LOG_TAG, "Battery Metering paused");
                        } else {
                            Intent batteryStatus = BatteryForegroundService.this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


                            metering.addMeteringEvent(
                                batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1),
                                batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1),
                                batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                                batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER),
                                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE),
                                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW),
                                batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
                            );
                        }

                        Thread.sleep(interval);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

                Log.i(LOG_TAG, "Stop metering session");
                metering.stopSession();
                metering.close();
                BatteryForegroundService.this.stopForeground(true);
                BatteryForegroundService.this.stopSelf();
            }
        });
        worker.start();
    }

    protected Notification createNotification() {
        Intent pauseIntent = new Intent(this, BatteryForegroundService.class);
        pauseIntent.setAction(ACTION_PAUSE_METERING);
        PendingIntent pendingPauseIntent = PendingIntent.getService(this, 1, pauseIntent, 0);

        Intent unpauseIntent = new Intent(this, BatteryForegroundService.class);
        unpauseIntent.setAction(ACTION_UNPAUSE_METERING);
        PendingIntent pendingUnpauseIntent = PendingIntent.getService(this, 2, unpauseIntent, 0);

        Intent stopIntent = new Intent(this, BatteryForegroundService.class);
        stopIntent.setAction(ACTION_STOP_METERING);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 3, stopIntent, 0);

        // default content intent
        Intent openApp = new Intent(this, MainActivity.class);
        PendingIntent pendingOpenApp = PendingIntent.getActivity(this, 0, openApp, 0);

        NotificationCompat.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Battery-Meter", "Battery Meter", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Battery Meter Notification Channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(this, channel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        if (state.equals("STARTED")) {
            notificationBuilder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPauseIntent));
            notificationBuilder.addAction(new NotificationCompat.Action(android.R.drawable.ic_notification_clear_all, "Stop", pendingStopIntent));
            notificationBuilder.setContentTitle("Battery Metering Service");
            notificationBuilder.setContentText("Battery Statistics are logged in the background");
            notificationBuilder.setTicker("Battery Metering Service is running");
        } else if (state.equals("STOPPED")) {
            notificationBuilder.setContentTitle("Battery Metering Service (Stopping)");
            notificationBuilder.setContentText("Battery Metering Service is stopping");
            notificationBuilder.setTicker("Battery Metering Service is stopping");
        } else {
            notificationBuilder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Unpause", pendingUnpauseIntent));
            notificationBuilder.addAction(new NotificationCompat.Action(android.R.drawable.ic_notification_clear_all, "Stop", pendingStopIntent));
            notificationBuilder.setContentTitle("Battery Metering Service (Paused)");
            notificationBuilder.setContentText("Battery Statistics logging is paused");
            notificationBuilder.setTicker("Battery Metering Service is paused");
        }

        notificationBuilder.setContentIntent(pendingOpenApp);
        notificationBuilder.setSmallIcon(R.drawable.ic_notify);
        return notificationBuilder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        worker = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // not a binding service
        return null;
    }
}
