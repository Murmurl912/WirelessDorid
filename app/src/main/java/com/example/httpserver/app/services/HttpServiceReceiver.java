package com.example.httpserver.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HttpServiceReceiver extends BroadcastReceiver {
    public static final String TAG = HttpServiceReceiver.class.getName();

    public static final String ACTION_STOP_SERVER = "com.example.httpserver.app.services.ACTION_STOP_SERVER";
    public static final String ACTION_START_SERVER = "com.example.httpserver.app.services.ACTION_START_SERVER";
    public static final String ACTION_RESTART_SERVER = "com.example.httpserver.app.services.ACTION_RESTART_SERVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received: " + intent.getAction());

        try {
            Intent service = new Intent(context, HttpService.class);
            service.putExtras(intent);
            String action = intent.getAction();
            if(action == null) {
                return;
            }
            switch (action) {
                case ACTION_START_SERVER:
                    context.startService(service);
                    break;
                case ACTION_STOP_SERVER:
                    context.stopService(service);
                    break;
                case ACTION_RESTART_SERVER:
                    context.stopService(service);
                    context.startService(service);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to start/stop on intent " + e.getMessage());
        }
    }
}
