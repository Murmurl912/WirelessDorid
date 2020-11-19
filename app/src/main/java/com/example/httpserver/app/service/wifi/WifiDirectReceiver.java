package com.example.httpserver.app.service.wifi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.util.Log;
import android.widget.Toast;

public class WifiDirectReceiver extends BroadcastReceiver {

    public static final String TAG = WifiDirectReceiver.class.getSimpleName();

    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final Service service;
    private int count = 0;

    public WifiDirectReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Service service) {
        this.manager = manager;
        this.channel = channel;
        this.service = service;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                if (count++ > 0) {
                    return;
                }

                Toast.makeText(context, "WiFi Direct Enabled", Toast.LENGTH_SHORT).show();

                manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "WiFi Direct Group Created", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Wifi Direct Group Created");
                    }

                    @Override
                    public void onFailure(int reason) {
                        String str = reason == WifiP2pManager.P2P_UNSUPPORTED ?
                                "Unsupported" : reason == WifiP2pManager.ERROR ?
                                "Error" : reason == WifiP2pManager.BUSY ?
                                "Busy" : reason + "";

                        Toast.makeText(context, "WiFi Direct Group Creation Filed: " + str, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Failed To Create WiFi Group: " + str);
                    }
                });


            } else {
                Toast.makeText(context, "WiFi Direct Disabled", Toast.LENGTH_SHORT).show();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed! We should probably do something about
            // that.
            Toast.makeText(context, "WiFi Direct Peer Changed", Toast.LENGTH_SHORT).show();

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.
            Toast.makeText(context, "WiFi Direct Connection Changed", Toast.LENGTH_SHORT).show();
            WifiP2pGroup wifiP2pGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(wifiP2pGroup != null) {
                String passphrase = wifiP2pGroup.getPassphrase();
                String network = wifiP2pGroup.getNetworkName();
                Log.d(TAG, "WiFi: " + network + ", " + passphrase);
            }
            Log.d(TAG, "Connection Changed: " + "\nWifiP2pInfo: " + wifiP2pInfo + "\nNetworkInfo: " + networkInfo + "\nWifiP2pGroupInfo: " + wifiP2pGroup);

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Toast.makeText(context, "WiFi Direct Self Changed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Device Changed: " + device);
        }
    }
}
