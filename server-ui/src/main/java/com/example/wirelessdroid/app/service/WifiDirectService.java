package com.example.wirelessdroid.app.service;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import androidx.core.app.ActivityCompat;

import java.util.function.Consumer;


public class WifiDirectService {

    private final Context context;
    private final WifiP2pReceiver receiver;
    private final WifiP2pManager manager;

    private volatile WifiP2pManager.Channel channel;
    private volatile WifiP2pGroup group;

    private volatile Boolean isEnabled;
    private volatile Runnable pendingStartup;


    public WifiDirectService(Context context) {
        this.context = context;
        receiver = new WifiP2pReceiver();
        manager = context.getSystemService(WifiP2pManager.class);
        receiver.register();
    }

    synchronized public void startup() {
        if (isEnabled == null) {
            status(1); // wifi direct enabled
            pendingStartup = this::startup;
            return;
        }

        if (!isEnabled) {
            status(-1); // wifi direct not enabled
            return;
        }

        if (channel == null) {
            try {
                channel = manager.initialize(context, context.getMainLooper(), () -> {
                    channel = null;
                    status(-2);
                });
                status(2);
            } catch (Exception e) {
                status(-3);
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            status(-4); // permission not granted
            return;
        }

        try { // prevent channel closed
            manager.requestGroupInfo(channel, group -> {
                WifiDirectService.this.group = group;
                if (group != null) {
                    status(3); // group created
                    return;
                }
                try { // prevent channel closed
                    manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            status(3); // group created
                        }

                        @Override
                        public void onFailure(int reason) {
                            status(-5); // failed to create group
                        }
                    });
                } catch (Exception e) {
                    status(-5);
                }
            });
        } catch (Exception e) {
            status(-5);
        }

    }

    synchronized public void shutdown() {
        pendingStartup = null;
        if (channel != null) {
            channel.close();
            group = null;
        }
        receiver.unregister();
    }

    synchronized protected void onEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (isEnabled && pendingStartup != null) {
            pendingStartup.run();
            pendingStartup = null;
        }

    }

    protected void status(int status) {

    }

    public WifiP2pGroup group(Consumer<WifiP2pGroup> groupConsumer) {
        if(channel == null) {
            return null;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                groupConsumer.accept(group);
            }
        });
        return group;
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

            context.registerReceiver(this, filter);
        }

        public void unregister() {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null) {
                return;
            }

            switch (intent.getAction()) {
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED);
                    onEnabled(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:

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
            }
        }
    }

}
