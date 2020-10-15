package com.example.httpserver.app.ui.view.server;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.TimeBasedOneTimePassword;
import com.example.httpserver.app.repository.TotpRepository;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.repository.entity.ServerConfig;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ServerViewModel extends ViewModel {
    public static final String TAG = ServerViewModel.class.getName();

    private MutableLiveData<String> password;
    private MutableLiveData<String> username;
    private MutableLiveData<String> url;
    private MutableLiveData<Integer> httpPort;
    private MutableLiveData<String> address;
    private MutableLiveData<List<String>> addresses;
    private MutableLiveData<String> pin;
    private MutableLiveData<Integer> progress;
    private MutableLiveData<Boolean> totp;
    private MutableLiveData<Boolean> tls;
    private MutableLiveData<Boolean> basic;
    private MutableLiveData<Integer> ftpPort;
    private MutableLiveData<Boolean> ftp;
    private MutableLiveData<Boolean> http;
    private Timer timer;
    private long start = 0L;
    private TimeBasedOneTimePassword totpPassword;
    private TimerTask task;

    private final Observer<Boolean> totpObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if(aBoolean) {
                if(timer == null) {
                    timer = new Timer();
                }
                if(task != null) {
                    task.cancel();
                }
                task = task();
                try {
                    timer.scheduleAtFixedRate(task, 0, 500);
                    Log.i(TAG, "Totp update task scheduled");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to schedule totp update task", e);
                }
                start = System.currentTimeMillis();
            }
        }
    };

    public ServerViewModel() {
        password = new MutableLiveData<>();
        username = new MutableLiveData<>();
        url = new MutableLiveData<>();
        httpPort = new MutableLiveData<>();
        address = new MutableLiveData<>();
        pin = new MutableLiveData<>();
        progress = new MutableLiveData<>();
        totp = new MutableLiveData<>();
        addresses = new MutableLiveData<>();
        tls = new MutableLiveData<>();
        basic = new MutableLiveData<>();
        ftpPort = new MutableLiveData<>();
        http = new MutableLiveData<>();
        ftp = new MutableLiveData<>();
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

    public MutableLiveData<Integer> httpPort() {
        return httpPort;
    }

    public MutableLiveData<Integer> ftpPort() {
        return ftpPort;
    }

    public MutableLiveData<Boolean> ftp() {
        return ftp;
    }

    public MutableLiveData<Boolean> http() {
        return http;
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

    public void refresh() {
        Log.i(TAG, "Refresh view model requested");
        App.app().executor().submit(()->{
            try {
                ServerConfig config = ServerConfig.from(App.app().db().configuration().select(ServerConfig.keys));
                Log.i(TAG, "Load server config from database successfully: " + config);
                if(config.totp) {
                    try {
                        totpPassword = TotpRepository.instance().getDefault();
                    } catch (NoSuchAlgorithmException e) {
                        Log.e(TAG, "Failed to obtain Totp password", e);
                        try {
                            Log.i(TAG, "Change totp config to false");
                            App.app().db().configuration().save(new Configuration("totp", "false"));
                        } catch (Exception ex) {
                            Log.e(TAG, "Failed to write config to database", ex);
                        }
                        config.totp = false;
                    }
                }

                username.postValue(config.username);
                password.postValue(config.password);
                ftpPort.postValue(config.ftp_port);
                httpPort.postValue(config.http_port);
                address.postValue(config.address);
                url.postValue("http://" + config.address + ":" + config.http_port + "/");
                totp.postValue(config.totp);
                tls.postValue(config.tls);
                basic.postValue(config.basic);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load server config from database", e);
            }

        });

        App.app().executor().submit(()->{
            try {
                Log.i(TAG, "try to load network address");
                List<String> networks = networks();
                Log.i(TAG, "Load network address successfully： " + networks);
                addresses.postValue(networks);
            } catch (Exception e) {
                Log.e(TAG, "Failed to get network address", e);
            }
        });

        totp.observeForever(totpObserver);
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

    private TimerTask task() {
        Log.i(TAG, "Create totp update timer task");
        return new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if(totpPassword == null) {
                    return;
                }

                int number = 0;
                int time = (int)(System.currentTimeMillis() % 30000);
                if(Math.abs(time - 30000) < 2000 || System.currentTimeMillis() - start < 2000) {
                    number = totpPassword.pin();
                    pin.postValue(String.format("%03d %03d", number / 1000, number % 1000));
                }

                progress.postValue(time);
            }
        };
    }
    @Override
    protected void onCleared() {
        timer.cancel();
        timer = null;
        totp.removeObserver(totpObserver);
        Log.d(TAG,  "View model is cleared");
        super.onCleared();
    }
}