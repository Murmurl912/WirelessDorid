package com.example.httpserver.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.widget.Toast;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.server.NettyHttpServer;

import java.io.IOException;

public class HttpService extends Service {

    private WebServer server;

    public HttpService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        App.app().executor().submit(()->{
            try {
                App.app().config().set("status", "starting");
                server.start();
                App.app().config().set("status", "running");
            } catch (IOException e) {
                App.app().config().set("status", "error");
            }
        });
        return START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        server = new WebServer(8080);
    }

    @Override
    public void onDestroy() {

        App.app().executor().submit(()->{
            App.app().config().set("status", "stopping");
            server.stop();
            App.app().config().set("status", "stopped");
        });
        super.onDestroy();
    }

}
