package com.example.httpserver.app.repository.entity;

import androidx.lifecycle.MutableLiveData;

public class LiveServerConfig {
    public MutableLiveData<Integer> port = new MutableLiveData<>(8080);
    public MutableLiveData<String> address = new MutableLiveData<>("localhost");
    public MutableLiveData<Boolean> basic = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> totp = new MutableLiveData<>(true);
    public MutableLiveData<Boolean> tls = new MutableLiveData<>(true);
    public MutableLiveData<String> key = new MutableLiveData<>("default");
    public MutableLiveData<String> cert = new MutableLiveData<>("default");
    public MutableLiveData<String> username = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");

    public static LiveServerConfig from(ServerConfig config) {
        LiveServerConfig live = new LiveServerConfig();
        live.port.postValue(config.port);
        live.address.postValue(config.address);
        live.basic.postValue(config.basic);
        live.totp.postValue(config.totp);
        live.tls.postValue(config.tls);
        live.key.postValue(config.key);
        live.cert.postValue(config.cert);
        live.username.postValue(config.username);
        live.password.postValue(config.password);
        return live;
    }
}
