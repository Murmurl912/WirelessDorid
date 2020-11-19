package com.example.httpserver.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import androidx.room.Room;
import com.example.httpserver.app.database.AppDatabase;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    public static final String TAG = App.class.getName();
    private static App app;
    private AppDatabase db;
    private ExecutorService executor;

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
        Date date = new Date();
        Log.i(TAG, "start setup app executor and database at " + date);
        executor = Executors.newCachedThreadPool();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();

        Log.i(TAG, "finish setup app executor and database at " + new Date() + ", duration: " + (System.currentTimeMillis() - date.getTime()) + "ms");
    }

    public AppDatabase db() {
        return db;
    }

    public ExecutorService executor() {
        return executor;
    }


}
