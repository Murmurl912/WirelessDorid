package com.example.httpserver.app.ui.view.server;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.httpserver.app.App;
import com.example.httpserver.app.LiveServerConfig;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.net.NetworkInterface.getNetworkInterfaces;

public class ServerViewModel extends ViewModel {

    public ServerViewModel() {

    }

    public LiveServerConfig config() {
        return App.app().config();
    }

    public LiveData<List<String>> address() {
        MutableLiveData<List<String>> address = new MutableLiveData<>();

        App.app().executor().submit(()->{
            List<String> networks = networks();
            address.postValue(networks);
        });

        return address;
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
                    if(!inetAddress.isSiteLocalAddress())
                        continue;
                    if(inetAddress instanceof Inet6Address)
                        continue;
                    if(inetAddress.isLinkLocalAddress())
                        continue;
                    addresses.add(inetAddress.getHostAddress());
                }
            }

        } catch (SocketException ignored) {

        }

        return addresses;
    }
}