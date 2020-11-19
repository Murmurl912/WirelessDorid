package com.example.httpserver.app.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.*;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class ManageService extends Service {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private volatile boolean channelAlive = false;
    private WifiP2pReceiver receiver;
    private volatile boolean groupCreated = false;
    private WifiP2pGroup group;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = getSystemService(WifiP2pManager.class);
        receiver = new WifiP2pReceiver();
        receiver.register();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notification();
        wifi();
        service();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        receiver.unregister();
    }

    private void notification() {

    }

    @SuppressLint("MissingPermission")
    private void wifi() {
        if(channel != null && channelAlive) {
            // created
            return;
        }

        channel = manager.initialize(this, getMainLooper(), () -> {
            // todo notify wifi direct channel closed
            channel = null;
            channelAlive = false;
        });
        channelAlive = true;
    }

    private void service() {

    }

    @SuppressLint("MissingPermission")
    private void createGroup() {
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                // todo report error
            }
        });
    }

    private class WifiP2pReceiver extends BroadcastReceiver {

        public void register() {
            IntentFilter filter = new IntentFilter();

            // Indicates a change in the Wi-Fi P2P status.
            filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

            // Indicates a change in the list of available peers.
            filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

            // Indicates the state of Wi-Fi P2P connectivity has changed.
            filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

            // Indicates this device's details have changed.
            filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            filter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

            registerReceiver(this, filter);
        }

        public void unregister() {
            unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null) {
                // todo handle exception
                return;
            }

            switch (intent.getAction()) {
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        createGroup();
                    } else {
                        // todo report error
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    WifiP2pDeviceList peers = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                    break;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    WifiP2pGroup group = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                    WifiP2pInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                    NetworkInfo network = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    break;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    break;
                case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                    int discovery = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
                    break;
                default:
                    // todo handle exception
                    return;
            }
        }
    }

}
