package com.example.httpserver.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.httpserver.app.App;

import java.io.IOException;

public class HttpService extends Service {

    private WebHttpServer server;

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
                App.app().serverStatus().postValue("starting");
                server.start();
                App.app().serverStatus().postValue("running");
            } catch (IOException e) {
                App.app().serverStatus().postValue("error");
            }
        });
        return START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        server = new WebHttpServer();
    }

    @Override
    public void onDestroy() {

        App.app().executor().submit(()->{
            App.app().serverStatus().postValue("stopping");
            server.stop();
            App.app().serverStatus().postValue("stopped");
        });
        super.onDestroy();
    }

}
