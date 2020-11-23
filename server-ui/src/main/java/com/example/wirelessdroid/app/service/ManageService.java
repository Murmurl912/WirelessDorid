package com.example.wirelessdroid.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class ManageService extends Service {

    public static final String ACTION_START_ALL = "ACTION_START_ALL";
    public static final String ACTION_STOP_ALL = "ACTION_STOP_ALL";

    public static final String ACTION_START_WIFI_DIRECT_SERVICE = "ACTION_START_WIFI_DIRECT_SERVICE";
    public static final String ACTION_STOP_WIFI_DIRECT_SERVICE = "ACTION_STOP_WIFI_DIRECT_SERVICE";

    public static final String ACTION_START_STORAGE_SERVICE = "ACTION_START_STORAGE_SERVICE";
    public static final String ACTION_STOP_STORAGE_SERVICE = "ACTION_STOP_STORAGE_SERVICE";

    private WifiDirectService wifi;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            default:
                startDefault();
                break;
            case ACTION_STOP_ALL:
                stopDefault();
                break;

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDefault();
    }

    protected void startDefault() {
       startWifiDirect();
    }

    protected void stopDefault() {
        stopWifiDirect();
    }

    private void notification() {

    }

    private void startWifiDirect() {
        if(wifi == null) {
            wifi = new WifiDirectService(this);
        }
        wifi.startup();
    }

    private void stopWifiDirect() {
        if(wifi != null) {
            wifi.shutdown();
        }
    }

    private void startStorage() {

    }

    private void stopStorage() {

    }


    public static void start(Context context) {
        Intent intent = new Intent(context, ManageService.class);
        intent.setAction(ACTION_START_ALL);
        context.startForegroundService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, ManageService.class);
        intent.setAction(ACTION_STOP_ALL);
        context.stopService(intent);
    }

}
