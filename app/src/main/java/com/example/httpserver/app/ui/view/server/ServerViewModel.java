package com.example.httpserver.app.ui.view.server;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.httpserver.app.App;
import com.example.httpserver.app.LiveServerConfig;
import com.example.httpserver.app.repository.entity.ServerConfig;

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

    private MutableLiveData<String> password;
    private MutableLiveData<String> username;
    private MutableLiveData<String> url;
    private MutableLiveData<Integer> port;
    private MutableLiveData<String> address;
    private MutableLiveData<List<String>> addresses;
    private MutableLiveData<String> pin;
    private MutableLiveData<Integer> progress;
    private MutableLiveData<Boolean> totp;
    private MutableLiveData<Boolean> tls;
    private MutableLiveData<Boolean> basic;

    public ServerViewModel() {
        password = new MutableLiveData<>();
        username = new MutableLiveData<>();
        url = new MutableLiveData<>();
        port = new MutableLiveData<>();
        address = new MutableLiveData<>();
        pin = new MutableLiveData<>();
        progress = new MutableLiveData<>();
        totp = new MutableLiveData<>();
        addresses = new MutableLiveData<>();
        tls = new MutableLiveData<>();
        basic = new MutableLiveData<>();
    }

    public MutableLiveData<String> password() {
        return password;
    }

    public MutableLiveData<String> username() {
        return username;
    }

    public MutableLiveData<String> url() {
        return url;
    }

    public MutableLiveData<Integer> port() {
        return port;
    }

    public MutableLiveData<String> address() {
        return address;
    }

    public MutableLiveData<String> pin() {
        return pin;
    }

    public MutableLiveData<Integer> progress() {
        return progress;
    }

    public MutableLiveData<Boolean> totp() {
        return totp;
    }

    public LiveData<List<String>> addresses() {
        return addresses;
    }

    public MutableLiveData<Boolean> tls() {
        return tls;
    }

    public MutableLiveData<Boolean> basic() {
        return basic;
    }


    public LiveData<Boolean> refresh() {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        App.app().executor().submit(()->{
            ServerConfig config = ServerConfig.from(App.app().db().configuration().select(ServerConfig.keys));
            username.postValue(config.username);
            password.postValue(config.password);
            port.postValue(config.port );
            address.postValue(config.address);
            url.postValue("http://" + config.address + ":" + config.port + "/");
            totp.postValue(config.totp);
            tls.postValue(config.tls);
            basic.postValue(config.basic);
            result.postValue(true);
        });

        App.app().executor().submit(()->{
            List<String> networks = networks();
            addresses.postValue(networks);
        });
        return result;
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