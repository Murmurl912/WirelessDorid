package com.example.httpserver.app.ui.view.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.service.event.ServiceEvent;
import org.greenrobot.eventbus.EventBus;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.function.Consumer;

public class ServicesViewModel extends ViewModel {

    private final MutableLiveData<List<Map<String, String>>> services;
    private final MutableLiveData<List<Map<String, String>>> networks;

    private final Consumer<ServiceEvent> serviceEventConsumer = serviceEvent -> {

    };

    public ServicesViewModel() {
        networks = new MutableLiveData<>();
        services = new MutableLiveData<>();
        EventBus.getDefault().register(serviceEventConsumer);
    }


    public LiveData<List<Map<String, String>>> network() {
        networks.postValue(networks());
        return networks;
    }

    public LiveData<List<Map<String, String>>> service() {
        services.postValue(services());
        return services;
    }

    public void refresh() {
        network();
        service();
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
        EventBus.getDefault().cancelEventDelivery(serviceEventConsumer);
    }
}