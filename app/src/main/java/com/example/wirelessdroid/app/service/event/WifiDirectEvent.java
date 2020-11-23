package com.example.wirelessdroid.app.service.event;

import android.os.Bundle;

public enum WifiDirectEvent implements ServiceEvent {

    WIFI_DIRECT_CHANNEL_CLOSED(null),
    WIFI_DIRECT_CHANNEL_OPEN(null),
    WIFI_DIRECT_ENABLED(null),
    WIFI_DIRECT_DISABLED(null),
    WIFI_DIRECT_GROUP_CREATE_SUCCESS(null),
    WIFI_DIRECT_GROUP_CREATE_ERROR(null),
    WIFI_DIRECT_CONNECTION_CHANGED(null),
    WIFI_DIRECT_PEERS_CHANGED(null),
    WIFI_DIRECT_DEVICE_CHANGED(null),
    WIFI_DIRECT_DISCOVERY_CHANGED(null);

    private Bundle extras;

    WifiDirectEvent(Bundle extras) {
        this.extras = extras;
    }

    @Override
    public Bundle extras(Bundle bundle) {
        Bundle old = this.extras;
        this.extras = bundle;
        return old;
    }

}
