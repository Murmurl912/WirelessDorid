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
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.example.httpserver.app.service.event.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ManageService extends Service {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private volatile boolean channelAlive = false;
    private WifiP2pReceiver receiver;
    private volatile boolean groupCreating = false;
    private volatile boolean groupCreated = false;
    private Handler handler;
    private long createDelay = 1000;

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
        handler = new Handler(getMainLooper());
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
        shutdownWifi();
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
            EventBus.getDefault().post(WifiDirectStatus.STATUS_OPENING);
            channel = manager.initialize(this, getMainLooper(), () -> {
                // todo create event builder
                channel = null;
                channelAlive = false;
                EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_CHANNEL_CLOSED);
                EventBus.getDefault().post(WifiDirectStatus.STATUS_CLOSED);
            });
            channelAlive = true;
            EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_CHANNEL_OPEN);
            EventBus.getDefault().post(WifiDirectStatus.STATUS_OPENED);
        } catch (Exception e) {
            // todo change bundle key
            Bundle statusBundle = new Bundle();
            statusBundle.putString("reason", e.toString());
            WifiDirectStatus status = WifiDirectStatus.STATUS_ERROR;
            status.extras(statusBundle);
            EventBus.getDefault().post(status);
        }
    }

    private void service() {

    }

    private void shutdownWifi() {
        if(manager == null || channel == null) {
            return;
        }
        EventBus.getDefault().post(WifiDirectStatus.STATUS_CLOSING);
        channel.close();
    }

    private void shutdownService() {

    }


    @SuppressLint("MissingPermission")
    private void createGroup() {
        if (groupCreated || groupCreating) {
            return;
        }

        groupCreating = true;
        EventBus.getDefault().post(WifiDirectStatus.STATUS_STARTING);
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                manager.createGroup(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_GROUP_CREATE_SUCCESS);
                        groupCreating = false;
                        groupCreated = true;
                        EventBus.getDefault().post(WifiDirectStatus.STATUS_STARTED);
                    }

                    @Override
                    public void onFailure(int reason) {
                        WifiDirectEvent event = WifiDirectEvent.WIFI_DIRECT_GROUP_CREATE_ERROR;

                        Bundle bundle = new Bundle();
                        bundle.putInt("reason", reason);
                        event.extras(bundle);
                        EventBus.getDefault().post(event);
                        groupCreating = false;

                        Bundle statusBundle = new Bundle();
                        statusBundle.putString("reason", Integer.toString(reason));
                        WifiDirectStatus status = WifiDirectStatus.STATUS_ERROR;
                        status.extras(bundle);
                        EventBus.getDefault().post(status);
                    }
                });
            }

            @Override
            public void onFailure(int i) {
                EventBus.getDefault().post(WifiDirectStatus.STATUS_ERROR);
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

        @SuppressWarnings("MissingPermission")
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
                        handler.postDelayed(ManageService.this::createGroup, createDelay);
                        EventBus.getDefault().post(WifiDirectStatus.STATUS_ENABLED);
                    } else {
                        EventBus.getDefault().post(WifiDirectEvent.WIFI_DIRECT_DISABLED);
                        EventBus.getDefault().post(WifiDirectStatus.STATUS_DISABLED);
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
                    event = WifiDirectEvent.WIFI_DIRECT_CONNECTION_CHANGED;
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
