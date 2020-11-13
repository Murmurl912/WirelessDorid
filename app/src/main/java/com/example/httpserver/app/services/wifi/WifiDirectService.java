package com.example.httpserver.app.services.wifi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class WifiDirectService extends Service {

    private WifiP2pManager manager;
    private IntentFilter filter;
    private WifiP2pManager.Channel channel;
    private WifiDirectReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Wifi Direct Service Created", Toast.LENGTH_SHORT).show();
        filter = new IntentFilter();
        // Indicates a change in the Wi-Fi P2P status.
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = getSystemService(WifiP2pManager.class);
        channel = manager.initialize(getApplicationContext(), getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Toast.makeText(getApplicationContext(), "Wifi Direct Channel Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
        receiver = new WifiDirectReceiver(manager, channel, this);
        registerReceiver(receiver, filter);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
