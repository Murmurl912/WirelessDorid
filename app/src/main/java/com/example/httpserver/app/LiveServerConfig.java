package com.example.httpserver.app;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.repository.entity.ServerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class LiveServerConfig {

    protected MutableLiveData<Integer> port = new MutableLiveData<>(8080);
    protected MutableLiveData<String> address = new MutableLiveData<>("localhost");
    protected MutableLiveData<Boolean> basic = new MutableLiveData<>(false);
    protected MutableLiveData<Boolean> totp = new MutableLiveData<>(true);
    protected MutableLiveData<Boolean> tls = new MutableLiveData<>(true);
    protected MutableLiveData<String> key = new MutableLiveData<>("default");
    protected MutableLiveData<String> cert = new MutableLiveData<>("default");
    protected MutableLiveData<String> username = new MutableLiveData<>("");
    protected MutableLiveData<String> password = new MutableLiveData<>("");
    protected MutableLiveData<String> status = new MutableLiveData<>("stopped");

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
        live.status.postValue(config.status);
        return live;
    }

    public List<Configuration> to() {
        ArrayList<Configuration> configurations = new ArrayList<>();
        configurations.add(new Configuration("port", port.getValue() + ""));
        configurations.add(new Configuration("address", address.getValue() + ""));
        configurations.add(new Configuration("basic", basic.getValue() + ""));
        configurations.add(new Configuration("totp", totp.getValue() + ""));
        configurations.add(new Configuration("tls", tls.getValue() + ""));
        configurations.add(new Configuration("totp_key", key.getValue()));
        configurations.add(new Configuration("tls_cert", cert.getValue()));
        configurations.add(new Configuration("username", username.getValue()));
        configurations.add(new Configuration("password", password.getValue()));
        configurations.add(new Configuration("status", status.getValue()));
        return configurations;
    }

    public void observe(LifecycleOwner owner, BiConsumer<String, Object> consumer) {
        port.observe(owner, v -> {
            consumer.accept("port", v);
        });
        address.observe(owner, v -> consumer.accept("address", v));
        basic.observe(owner, v -> consumer.accept("basic", v));
        totp.observe(owner, v -> consumer.accept("totp", v));
        tls.observe(owner, v -> consumer.accept("tls", v));
        key.observe(owner, v -> consumer.accept("totp_key", v));
        cert.observe(owner, v -> consumer.accept("tls_cert", v));
        username.observe(owner, v -> consumer.accept("username", v));
        password.observe(owner, v -> consumer.accept("password", v));
        status.observe(owner, v -> consumer.accept("status", v));
    }

    public void set(String key, Object value) {
        App.app().executor().submit(()->{
            App.app().db().configuration().save(new Configuration(key, value.toString()));
            switch (key) {
                case "port":
                    port.postValue(Integer.parseInt(value.toString()));
                    break;
                case "address":
                    address.postValue(value.toString());
                    break;
                case "status":
                    status.postValue(value.toString());
                    break;
                case "basic":
                    basic.postValue(Boolean.parseBoolean(value.toString()));
                    break;
                case "totp":
                    totp.postValue(Boolean.parseBoolean(value.toString()));
                    break;
                case "tls":
                    tls.postValue(Boolean.parseBoolean(value.toString()));
                    break;
                case "username":
                    username.postValue(value.toString());
                    break;
                case "password":
                    password.postValue(value.toString());
                    break;
            }
        });
    }

    public Object get(String key) {
        switch (key) {
            case "port":
                return port.getValue();
            case "address":
                return address.getValue();
            case "status":
                return status.getValue();
            case "basic":
                return basic.getValue();
            case "totp":
                return totp.getValue();
            case "tls":
                return tls.getValue();
            case "username":
                return username.getValue();
            case "password":
                return password.getValue();
        }
        return null;
    }

    public void assign(List<Configuration> configurations) {
        for (Configuration configuration : configurations) {
            String value = configuration.value;
            switch (configuration.key) {
                case "port":
                    port.postValue(Integer.parseInt(value));
                    break;
                case "address":
                    address.postValue(value);
                    break;
                case "basic":
                    basic.postValue(Boolean.parseBoolean(value));
                    break;
                case "totp":
                    totp.postValue(Boolean.parseBoolean(value));
                    break;
                case "tls":
                    tls.postValue(Boolean.parseBoolean(value));
                    break;
                case "totp_key":
                    key.postValue(value);
                    break;
                case "tls_cert":
                    cert.postValue(value);
                    break;
                case "username":
                    username.postValue(value);
                    break;
                case "password":
                    password.postValue(value);
                    break;
                case "status":
                    status.postValue(value);
            }
        }
    }

    public LiveData<Integer> port() {
        return port;
    }

    public LiveData<String> address() {
        return address;
    }

    public LiveData<Boolean> basic() {
        return basic;
    }

    public LiveData<Boolean> totp() {
        return totp;
    }

    public LiveData<Boolean> tls() {
        return tls;
    }

    public LiveData<String> key() {
        return key;
    }

    public LiveData<String> cert() {
        return cert;
    }

    public LiveData<String> username() {
        return username;
    }

    public LiveData<String> password() {
        return password;
    }

    public LiveData<String> status() {
        return status;
    }
}
