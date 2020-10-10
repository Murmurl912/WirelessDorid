package com.example.httpserver.app;

import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.httpserver.app.database.AppDatabase;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.app.services.HttpService;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application{

    private AppDatabase db;
    private LiveServerConfig config;
    private ExecutorService executor;

    private static App app;
    public static App app() {
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        setup();
        app = this;
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void setup() {
        executor = Executors.newCachedThreadPool();
        config = new LiveServerConfig();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();
        executor.submit(()->{
            db.configuration().save(new Configuration("status", "stopped"));
            List<Configuration> configs = db.configuration().select(ServerConfig.keys);
            config.assign(configs);
        });
    }

    public AppDatabase db() {
        return db;
    }

    public ExecutorService executor() {
        return executor;
    }

    public LiveServerConfig config() {
        return config;
    }

    public void start() {
        startService(new Intent(getApplicationContext(), HttpService.class));
    }

    public void stop() {
        stopService(new Intent(getApplicationContext(), HttpService.class));
    }

}
