package com.example.httpserver.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.httpserver.server.NettyHttpServer;

public class HttpService extends Service {

    private NettyHttpServer server;

    public HttpService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        server.start();
        return START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        server = new NettyHttpServer();
        server.init();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        server.stop();
        super.onDestroy();
    }

}
