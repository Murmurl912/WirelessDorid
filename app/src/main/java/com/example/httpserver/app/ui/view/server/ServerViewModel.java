package com.example.httpserver.app.ui.view.server;

import androidx.lifecycle.ViewModel;

import com.example.httpserver.app.App;
import com.example.httpserver.app.LiveServerConfig;

import java.util.List;

public class ServerViewModel extends ViewModel {


    public ServerViewModel() {

    }

    public LiveServerConfig config() {
       return App.app().config();
    }

    public List<String> address() {
        return null;
    }
}