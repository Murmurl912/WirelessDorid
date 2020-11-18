package com.example.httpserver.app.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.util.function.BiConsumer;

public class ServiceManager extends Service implements BiConsumer<Context, Intent> {

    private ServiceReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new ServiceReceiver();
        this.registerReceiver(receiver, receiver.filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void accept(Context context, Intent intent) {
        // todo handle action
    }

    private class ServiceReceiver extends BroadcastReceiver {

        public final IntentFilter filter = new IntentFilter();

        public ServiceReceiver() {
            // Indicates a change in the Wi-Fi P2P status.
            filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

            // Indicates a change in the list of available peers.
            filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

            // Indicates the state of Wi-Fi P2P connectivity has changed.
            filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

            // Indicates this device's details have changed.
            filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ServiceManager.this.accept(context, intent);
        }
    }

}
