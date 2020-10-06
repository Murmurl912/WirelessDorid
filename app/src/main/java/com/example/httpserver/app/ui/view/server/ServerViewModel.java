package com.example.httpserver.app.ui.view.server;

import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.LiveServerConfig;
import com.example.httpserver.app.repository.entity.ServerConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerViewModel extends ViewModel {

    private LiveServerConfig config;

    public ServerViewModel() {
        config = LiveServerConfig.from(new ServerConfig());
    }

    public CompletableFuture<LiveServerConfig> config() {
        return CompletableFuture.supplyAsync(() -> {
            config = LiveServerConfig.from(ServerConfig.from(App.db().configuration().select(ServerConfig.keys)));
            return config;
        }, App.executor);
    }

    public List<String> address() {
        return null;
    }
}