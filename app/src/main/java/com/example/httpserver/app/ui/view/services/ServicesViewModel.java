package com.example.httpserver.app.ui.view.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.App;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class ServicesViewModel extends ViewModel {

    private final MutableLiveData<List<String>> addresses;
    private final MutableLiveData<List<Map<String, String>>> services;

    public ServicesViewModel() {
        addresses = new MutableLiveData<>();
        services = new MutableLiveData<>();
    }

    public LiveData<List<String>> addresses() {
        App.app().executor().submit(()->{
            addresses.postValue(networks());
        });
        return addresses;
    }

    private List<String> networks() {
        List<String> addresses = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if(inetAddress instanceof Inet6Address)
                        continue;
                    if(inetAddress.isLoopbackAddress())
                        continue;
                    addresses.add(inetAddress.getHostAddress());
                }
            }

        } catch (SocketException ignored) {

        }

        return addresses;
    }

    public LiveData<List<Map<String, String>>> services() {
        App.app().executor().submit(()->{
            Map<String, String> http = new HashMap<>();
            http.put("title", "Http File Service");
            http.put("description", "Manage storage over http.");
            Map<String, String> ftp = new HashMap<>();
            ftp.put("title", "Ftp File Service");
            ftp.put("description", "Manage storage over ftp");

            services.postValue(Arrays.asList(http, ftp));
        });

        return services;
    }
}