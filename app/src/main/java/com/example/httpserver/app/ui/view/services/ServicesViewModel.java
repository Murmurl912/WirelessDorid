package com.example.httpserver.app.ui.view.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.App;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ServicesViewModel extends ViewModel {

    private final MutableLiveData<List<String>> addresses;

    public ServicesViewModel() {
        addresses = new MutableLiveData<>();
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
}