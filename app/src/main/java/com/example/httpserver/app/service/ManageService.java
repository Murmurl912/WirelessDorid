package com.example.httpserver.app.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.*;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.example.httpserver.app.service.event.*;
import org.greenrobot.eventbus.EventBus;

public class ManageService extends Service {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private volatile boolean channelAlive = false;
    private WifiP2pReceiver receiver;
    private volatile boolean groupCreating = false;
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

        try {
            channel = manager.initialize(this, getMainLooper(), () -> {
                // todo create event builder
                channel = null;
                channelAlive = false;
                EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_CHANNEL_CLOSED);
            });
            channelAlive = true;
            EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_CHANNEL_OPEN);
        } catch (Exception e) {
            // todo handle exception

        }
    }

    private void service() {

    }

    @SuppressLint("MissingPermission")
    private void createGroup() {
        if (groupCreated || groupCreating) {
            return;
        }

        groupCreating = true;
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_GROUP_CREATE_SUCCESS);
                groupCreating = false;
                groupCreated = true;
            }

            @Override
            public void onFailure(int reason) {
                WifiDirectEvent event = WifiDirectEvent.WIFI_DIRECT_GROUP_CREATE_ERROR;

                Bundle bundle = new Bundle();
                bundle.putInt("reason", reason);
                event.extras(bundle);
                EventBus.getDefault().post(event);
                groupCreating = false;
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
            Bundle bundle = new Bundle();
            WifiDirectEvent event;

            switch (intent.getAction()) {
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_ENABLED);
                        createGroup();
                    } else {
                        EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_DISABLED);
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    WifiP2pDeviceList peers = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                    bundle = new Bundle();
                    bundle.putParcelable("peers", peers);
                    event = WifiDirectEvent.WIFI_DIRECT_PEERS_CHANGED;
                    event.extras(bundle);
                    EventBus.getDefault().post(event);
                    break;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    WifiP2pGroup group = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                    WifiP2pInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                    NetworkInfo network = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                    bundle = new Bundle();
                    bundle.putParcelable("group", group);
                    bundle.putParcelable("info", info);
                    bundle.putParcelable("network", network);
                    event = WifiDirectEvent.WIFI_DIRECT_PEERS_CHANGED;
                    event.extras(bundle);
                    EventBus.getDefault().post(event);
                    break;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    bundle = new Bundle();
                    bundle.putParcelable("device", device);
                    event = WifiDirectEvent.WIFI_DIRECT_DEVICE_CHANGED;
                    event.extras(bundle);
                    EventBus.getDefault().post(event);
                    break;
                case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                    int discovery = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
                    bundle = new Bundle();
                    bundle.putInt("discovery", discovery);
                    event = WifiDirectEvent.WIFI_DIRECT_DISCOVERY_CHANGED;
                    event.extras(bundle);
                    EventBus.getDefault().post(event);
                    break;
                default:
                    // todo handle exception
            }
        }
    }

}
