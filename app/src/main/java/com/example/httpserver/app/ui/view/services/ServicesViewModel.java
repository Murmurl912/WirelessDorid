package com.example.httpserver.app.ui.view.services;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.core.app.BundleCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.service.event.ServiceEvent;
import com.example.httpserver.app.service.event.WifiDirectEvent;
import com.example.httpserver.app.service.event.WifiDirectStatus;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.function.Consumer;

public class ServicesViewModel extends ViewModel {

    private final MutableLiveData<List<Map<String, String>>> services;
    private final MutableLiveData<List<Map<String, String>>> networks;
    private final MutableLiveData<Bundle> wifi = new MutableLiveData<>();
    private final MutableLiveData<WifiDirectStatus> status = new MutableLiveData<>();

    public static final String STATUS_ENABLE = "Enabled";
    public static final String STATUS_DISABLE = "Disabled";
    public static final String STATUS_OPENING = "Opening";
    public static final String STATUS_CLOSING = "Closing";
    public static final String STATUS_STARTING = "Starting";
    public static final String STATUS_STARTED = "Started";
    public static final String STATUS_OPENED = "Opened";
    public static final String STATUS_CLOSED = "Closed";

    public ServicesViewModel() {
        networks = new MutableLiveData<>();
        services = new MutableLiveData<>();
        EventBus.getDefault().register(this);
    }

    public LiveData<List<Map<String, String>>> network() {
        networks.postValue(networks());
        return networks;
    }

    public LiveData<List<Map<String, String>>> service() {
        services.postValue(services());
        return services;
    }

    public LiveData<Bundle> wifi() {
        return wifi;
    }

    public LiveData<WifiDirectStatus> status() {
        return status;
    }

    public void refresh() {
        network();
        service();
    }


    @Subscribe
    public void onStatus(WifiDirectStatus status) {
        this.status.postValue(status);
    }

    @Subscribe
    public void onEvent(WifiDirectEvent event) {
        Bundle bundle = event.extras(null);
        switch (event) {
            case WIFI_DIRECT_CONNECTION_CHANGED:
                if(bundle == null) {
                    return;
                }
                wifi.postValue(bundle);
                break;
            case WIFI_DIRECT_CHANNEL_CLOSED:
            case WIFI_DIRECT_ENABLED:
            case WIFI_DIRECT_DISABLED:
            case WIFI_DIRECT_CHANNEL_OPEN:
            case WIFI_DIRECT_PEERS_CHANGED:
            case WIFI_DIRECT_DISCOVERY_CHANGED:
            case WIFI_DIRECT_DEVICE_CHANGED:
            case WIFI_DIRECT_GROUP_CREATE_ERROR:
            case WIFI_DIRECT_GROUP_CREATE_SUCCESS:
                break;
        }
    }

    private List<Map<String, String>> networks() {

        List<Map<String, String>> networks = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Map<String, String> network = new HashMap<>();
                network.put("name", networkInterface.getDisplayName());
                network.put("full_name", networkInterface.getName());
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress instanceof Inet6Address)
                        continue;
                    if (inetAddress.isLoopbackAddress())
                        continue;
                    network.put("address", inetAddress.getHostAddress());
                    networks.add(network);
                    break;
                }
            }

        } catch (SocketException ignored) {

        }

        return networks;
    }

    private List<Map<String, String>> services() {
        List<Map<String, String>> services = new ArrayList<>();

        Map<String, String> storage = new HashMap<>();
        storage.put("name", "Storage Manage Service");
        storage.put("description", "Manage phone storage via network.");

        Map<String, String> screen = new HashMap<>();
        screen.put("name", "Screen Cast Service");
        screen.put("description", "Cast screen via network");

        services.add(storage);
        services.add(screen);

        return services;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EventBus.getDefault().register(this);
    }
}