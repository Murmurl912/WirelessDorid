package com.example.httpserver.app;

import android.app.Application;
import android.content.Intent;
import androidx.room.Room;
import com.example.httpserver.app.database.AppDatabase;
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.app.services.HttpService;

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
        executor.submit(()->{
            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "app-database").build();
            config = LiveServerConfig
                    .from(ServerConfig
                            .from(db.configuration().select(ServerConfig.keys))
                    );
            config.set("status", "stopped");
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
