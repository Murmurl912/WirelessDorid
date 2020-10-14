package com.example.httpserver.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.app.services.http.server.TinyWebServer;
import com.example.httpserver.app.services.http.route.Router;

import java.util.Date;
import java.util.function.BiConsumer;

public class HttpService extends Service {

    private TinyWebServer server;
    private BiConsumer<Integer, Exception> listener = new BiConsumer<Integer, Exception>() {
        @Override
        public void accept(Integer integer, Exception exception) {
            switch (integer) {

            }
        }
    };

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
                ServerConfig config = ServerConfig.from(App.app().db().configuration().select(ServerConfig.keys));
                server.setConfig(config);
                server.router()
                        .add(Router.Route.of("GET", "/api/time", (session, map) -> TinyWebServer.response(new Date().toString())))
                        .add(Router.Route.of("GET", "/**", (session, map) -> TinyWebServer.response(map.toString())));
                server.start();
                App.app().serverStatus().postValue("running");
            } catch (Exception e) {
                App.app().serverStatus().postValue("error");
            }
        });
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        server = new TinyWebServer();
        server.setServerListener(listener);
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
