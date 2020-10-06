package com.example.httpserver.app.ui.view.server;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.repository.entity.LiveServerConfig;
import com.example.httpserver.app.repository.entity.ServerConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerViewModel extends ViewModel {

    private MutableLiveData<ServerConfig> config;
    private MutableLiveData<Boolean> ready;

    public ServerViewModel() {
        config = new MutableLiveData<>();
        ready = new MutableLiveData<>(false);
    }

    public MutableLiveData<ServerConfig> config() {
        App.executor.submit(()-> {
            List<Configuration> configurations = App.db().configuration().select(ServerConfig.keys);
            if(configurations.isEmpty()) {
                ServerConfig s = new ServerConfig();
                App.db().configuration().save(s.to());
                config.postValue(s);
            } else {
                config.postValue(ServerConfig.from(configurations));
            }

        });
        return config;
    }

    public List<String> address() {
        return null;
    }
}